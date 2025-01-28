package com.matchme.srv.service;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

import com.matchme.srv.dto.request.settings.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.matchme.srv.dto.request.*;
import com.matchme.srv.dto.response.*;
import com.matchme.srv.exception.DuplicateFieldException;
import com.matchme.srv.exception.InvalidVerificationException;
import com.matchme.srv.exception.ResourceNotFoundException;
import com.matchme.srv.mapper.AttributesMapper;
import com.matchme.srv.mapper.PreferencesMapper;
import com.matchme.srv.mapper.UserParametersMapper;
import com.matchme.srv.model.user.User;
import com.matchme.srv.model.user.UserAuth;
import com.matchme.srv.model.user.UserRoleType;
import com.matchme.srv.model.user.UserStateTypes;
import com.matchme.srv.model.user.activity.ActivityLog;
import com.matchme.srv.model.user.activity.ActivityLogType;
import com.matchme.srv.model.user.profile.Hobby;
import com.matchme.srv.model.user.profile.ProfileChange;
import com.matchme.srv.model.user.profile.ProfileChangeType;
import com.matchme.srv.model.user.profile.UserGenderType;
import com.matchme.srv.model.user.profile.UserProfile;
import com.matchme.srv.model.user.profile.user_attributes.AttributeChange;
import com.matchme.srv.model.user.profile.user_attributes.AttributeChangeType;
import com.matchme.srv.model.user.profile.user_attributes.UserAttributes;
import com.matchme.srv.model.user.profile.user_preferences.PreferenceChange;
import com.matchme.srv.model.user.profile.user_preferences.PreferenceChangeType;
import com.matchme.srv.model.user.profile.user_preferences.UserPreferences;
import com.matchme.srv.model.user.profile.user_score.UserScore;
import com.matchme.srv.repository.*;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final AccessValidationService accessValidationService;
  private final UserGenderTypeRepository genderRepository;
  private final UserRoleTypeRepository roleRepository;
  private final HobbyRepository hobbyRepository;

  private final AttributesMapper attributesMapper;
  private final PreferencesMapper preferencesMapper;
  private final UserParametersMapper parametersMapper;

  private final ActivityLogTypeRepository activityLogTypeRepository;
  private final AttributeChangeTypeRepository attributeChangeTypeRepository;
  private final PreferenceChangeTypeRepository preferenceChangeTypeRepository;
  private final ProfileChangeTypeRepository profileChangeTypeRepository;
  private final UserStateTypesRepository userStateTypesRepository;

  private final PasswordEncoder encoder;

  private static final String USER_NOT_FOUND_MESSAGE = "User not found!";
  private static final String VERIFIED = "VERIFIED";
  private static final String CREATED = "CREATED";

  public UserRoleType getDefaultRole() {

    String defaultRole = "ROLE_USER";

    return roleRepository.findByName(defaultRole) // TODO: Check this, talk in group
        .orElseThrow(() -> new RuntimeException("Default role not found"));
  }

  // Required, because we can't fetch default role from repository in entity class
  public void assignDefaultRole(User user) {
    UserRoleType defaultRole = getDefaultRole();
    user.setRole(defaultRole);
  }

  public UserGenderType getGender(Long genderId) {
      return genderRepository.findById(genderId)
              .orElseThrow(() -> new RuntimeException("Gender not found!"));
  }

  /**
   * Checks if the given email or phone number is used by a *different* user.
   * If so, throws a DuplicateFieldException.
   *
   * @param email    - new email to be used
   * @param number   - new phone number to be used
   * @param userId   - the user doing the update (can be null for new users)
   */
  private void validateUniqueEmailAndNumber(String email, String number, Long userId) {
    if (email != null && !email.isBlank()) {
      userRepository.findByEmail(email)
              .filter(existingUser -> !existingUser.getId().equals(userId))
              .ifPresent(u -> {
                throw new DuplicateFieldException("email", "Email already exists");
              });
    }

    if (number != null && !number.isBlank()) {
      userRepository.findByNumber(number)
              .filter(existingUser -> !existingUser.getId().equals(userId))
              .ifPresent(u -> {
                throw new DuplicateFieldException("number", "Phone number already exists");
              });
    }
  }

  // Creates User entity and UserAuth entity for user, sends verification e-mail
  @Transactional
  public ActivityLog createUser(SignupRequestDTO signUpRequest) {
    log.info("Creating user with email: " + signUpRequest.getEmail());

    validateUniqueEmailAndNumber(signUpRequest.getEmail(), signUpRequest.getNumber(), null);

    UserStateTypes state = userStateTypesRepository.findByName("UNVERIFIED")
        .orElseThrow(() -> new RuntimeException("UserState not found"));

    // Create new user (email, number, status) + state
    User newUser = new User(signUpRequest.getEmail(), signUpRequest.getNumber(), state);
    assignDefaultRole(newUser);

    // Create Auth entity for the user and assign it the given password encoded.
    UserAuth newAuth = new UserAuth(encoder.encode(signUpRequest.getPassword()));
    newUser.setUserAuth(newAuth);

    ActivityLogType logType = activityLogTypeRepository.findByName(CREATED)
        .orElseThrow(() -> new RuntimeException("LogType not found"));

    ActivityLog newEntry = new ActivityLog(newUser, logType);

    userRepository.save(newUser);

    log.info(newEntry.toString());

    // TODO: Send email verification to email

    log.info("User created: " + newUser);
    return newEntry;
  }

  // Verifies e-mail, creates UserProfile, UserAttributes and UserPreferences
  // entities for the user
  public void verifyAccount(Long userId, int verificationCode) {

    // Get user entity
    User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
    UserAuth auth = user.getUserAuth();

    // Verify account
    if (auth.getRecovery() == verificationCode) {
      user.setState(userStateTypesRepository.findByName(VERIFIED)
          .orElseThrow(() -> new RuntimeException("UserState not found")));
      auth.setRecovery(null);
      ActivityLogType logType = activityLogTypeRepository.findByName(VERIFIED)
          .orElseThrow(() -> new RuntimeException("LogType not found"));
      log.info(new ActivityLog(user, logType).toString());
    } else {
      throw new InvalidVerificationException("Verification code was wrong! Would you like us to generate the code again?");
    }

    // Create profile entity for user
    UserProfile newProfile = new UserProfile();
    user.setProfile(newProfile);
    ProfileChangeType profileType = profileChangeTypeRepository.findByName(CREATED)
        .orElseThrow(() -> new RuntimeException("Profile Change Type not found"));
    log.info(new ProfileChange(newProfile, profileType, null).toString());

    // Create attributes entity for user
    UserAttributes newAttributes = new UserAttributes();
    newProfile.setAttributes(newAttributes);
    AttributeChangeType attributeType = attributeChangeTypeRepository.findByName(CREATED)
        .orElseThrow(() -> new RuntimeException("Type not found"));
    log.info(new AttributeChange(newAttributes, attributeType, null).toString());

    // Create preferences entity for user
    UserPreferences newPreferences = new UserPreferences();
    newProfile.setPreferences(newPreferences);
    PreferenceChangeType preferenceChangeType = preferenceChangeTypeRepository.findByName(CREATED)
        .orElseThrow(() -> new RuntimeException("Preference Change Type not found"));
    log.info(new PreferenceChange(newPreferences, preferenceChangeType, null).toString());

    // Should cascade everything
    userRepository.save(user);
  }

  // Finish setting up account data after verifying email.
  public ActivityLog setUserParameters(Long userId, UserParametersRequestDTO parameters) {

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MESSAGE));

    UserProfile profile = user.getProfile();
    if (profile == null) {
      profile = new UserProfile();
      user.setProfile(profile);

      ProfileChangeType profileType = profileChangeTypeRepository.findByName(CREATED)
          .orElseThrow(() -> new ResourceNotFoundException("Profile Change Type not found"));
      new ProfileChange(profile, profileType, null);
    }

    UserAttributes attributes = profile.getAttributes();
    if (attributes == null) {
      attributes = new UserAttributes();
      profile.setAttributes(attributes);

      AttributeChangeType attributeType = attributeChangeTypeRepository.findByName(CREATED)
          .orElseThrow(() -> new ResourceNotFoundException("Attribute Change Type"));
      new AttributeChange(attributes, attributeType, null);
    }

    UserPreferences preferences = profile.getPreferences();
    if (preferences == null) {
      preferences = new UserPreferences();
      profile.setPreferences(preferences);

      PreferenceChangeType preferenceChangeType = preferenceChangeTypeRepository.findByName(CREATED)
          .orElseThrow(() -> new ResourceNotFoundException("Preference Change Type"));
      new PreferenceChange(preferences, preferenceChangeType, null);
    }

    attributesMapper.toEntity(attributes, parameters);
    attributes.setGender(getGender(parameters.gender_self()));
    attributes.setLocation(List.of(parameters.longitude(), parameters.latitude()));

    preferencesMapper.toEntity(preferences, parameters);
    preferences.setGender(getGender(parameters.gender_other()));

    profile.setFirst_name(parameters.first_name());
    profile.setLast_name(parameters.last_name());
    profile.setAlias(parameters.alias());
    profile.setCity(parameters.city());

    if (parameters.hobbies() != null && !parameters.hobbies().isEmpty()) {
        Set<Hobby> foundHobbies = parameters.hobbies().stream()
                .map(hobbyId -> hobbyRepository.findById(hobbyId)
                        .orElseThrow(() -> new EntityNotFoundException("Hobby not found with id: " + hobbyId)))
                .collect(Collectors.toSet());
        profile.setHobbies(foundHobbies);
    } else {
        profile.setHobbies(new HashSet<>());
    }

    user.setState(userStateTypesRepository.findByName("NEW")
        .orElseThrow(() -> new ResourceNotFoundException("User state")));

    ActivityLogType activitylogType = activityLogTypeRepository.findByName(VERIFIED)
        .orElseThrow(() -> new ResourceNotFoundException("LogType"));

    ActivityLog newEntry = new ActivityLog(user, activitylogType);

    // Now user can have a score entity and start looking for a chat
    UserScore score = new UserScore();
    user.setScore(score);

    userRepository.save(user);

    return newEntry;
  }

  public void updateAccountSettings(Long userId, AccountSettingsRequestDTO settings) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MESSAGE));

    log.info("Attempting to update account settings for userId: {}. New email: {}, new number: {}",
            userId, settings.getEmail(), settings.getNumber());

    validateUniqueEmailAndNumber(settings.getEmail(), settings.getNumber(), user.getId());

    user.setEmail(settings.getEmail());
    user.setNumber(settings.getNumber());

    userRepository.save(user);
    log.info("Successfully updated user with ID: {}", userId);
  }

  public void updateProfileSettings(Long userId, ProfileSettingsRequestDTO settings) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MESSAGE));

    UserProfile profile = user.getProfile();
    profile.setFirst_name(settings.getFirst_name());
    profile.setLast_name(settings.getLast_name());
    profile.setAlias(settings.getAlias());
    if (settings.getHobbies() != null && !settings.getHobbies().isEmpty()) {
        Set<Hobby> foundHobbies = settings.getHobbies().stream()
            .map(hobbyId -> hobbyRepository.findById(hobbyId)
                .orElseThrow(() -> new EntityNotFoundException("Hobby not found with id: " + hobbyId)))
            .collect(Collectors.toSet());
        
        profile.setHobbies(foundHobbies);
    } else {
        profile.setHobbies(new HashSet<>());
    }

    // TODO: Add logging
    userRepository.save(user);
  }

  public void updateAttributesSettings(Long userId, AttributesSettingsRequestDTO settings) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MESSAGE));

    UserProfile profile = user.getProfile();
    UserAttributes attributes = profile.getAttributes();

    attributesMapper.toEntity(attributes, settings);
    attributes.setGender(getGender(settings.getGender_self()));
    attributes.setLocation(List.of(settings.getLongitude(), settings.getLatitude()));

    profile.setCity(settings.getCity());

    // TODO: Add logging
    userRepository.save(user);
  }

  public void updatePreferencesSettings(Long userId, PreferencesSettingsRequestDTO settings) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MESSAGE));

    UserProfile profile = user.getProfile();
    UserPreferences preferences = profile.getPreferences();

    preferencesMapper.toEntity(preferences, settings);
    preferences.setGender(getGender(settings.getGender_other()));

    // TODO: Add logging
    userRepository.save(user);
  }

  public UserParametersResponseDTO getParameters(Long userId) {

    User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MESSAGE));
    UserAuth auth = user.getUserAuth();
    UserProfile profile = user.getProfile();
    UserAttributes attributes = profile.getAttributes();
    UserPreferences preferences = profile.getPreferences();

    return parametersMapper.toUserParametersDTO(user, attributes, preferences, auth);
  }

  public User getUser(Long userId) {
      return userRepository.findById(userId)
              .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MESSAGE));
  }

  public User getUserByEmail(String email) {
      return userRepository.findByEmail(email)
              .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MESSAGE));
  }

  public void removeUserByEmail(String email) {
    User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MESSAGE));
    userRepository.delete(user);
  }

  @Transactional
  public void saveProfilePicture(Long userId, ProfilePictureSettingsRequestDTO request) {
    User user = userRepository.findById(userId)
      .orElseThrow(() -> new EntityNotFoundException("User not found for ID: " + userId));

    UserProfile profile = user.getProfile();
    if (profile == null) {
        profile = new UserProfile();
        user.setProfile(profile);
    }

    // If request is null or base64Image is null/empty, remove the profile picture
    if (request == null || request.getBase64Image() == null || request.getBase64Image().isEmpty()) {
        profile.setProfilePicture(null);
    } else {
    String base64Part = extractBase64Part(request.getBase64Image());
    byte[] imageBytes = decodeBase64Image(base64Part);
    profile.setProfilePicture(imageBytes);
    }

    userRepository.save(user);
  }

  private String extractBase64Part(String base64Image) {
    return base64Image.contains(",") ? base64Image.substring(base64Image.indexOf(',') + 1) : base64Image;
  }

  private byte[] decodeBase64Image(String base64Part) {
    try {
      return Base64.getDecoder().decode(base64Part);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid Base64 image data.", e);
    }
  }

  //TODO: Add validateImageSize method to check Checks if the image exceeds 5 MB
  //TODO: validateImageFormat method to check that the image is either PNG or JPEG method.

  public void validateUserAccess(Long currentUserId, Long targetUserId) {
      accessValidationService.validateUserAccess(currentUserId, targetUserId);
  }

  /**
   * Return a user’s basic info,
   * first checks whether the current user can access the target’s data.
   */
  @Transactional(readOnly = true)
  public CurrentUserResponseDTO getUserDTO(Long currentUserId, Long targetUserId) {
    validateUserAccess(currentUserId, targetUserId);
    User user = userRepository.findById(targetUserId)
            .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MESSAGE));
    return buildCurrentUserResponseDTO(user);
  }

  /**
   * Build CurrentUserResponseDTO, factoring in profile picture, etc.
   */
  private CurrentUserResponseDTO buildCurrentUserResponseDTO(User user) {
    UserProfile profile = user.getProfile();

    String base64Picture = null;
    if (profile != null && profile.getProfilePicture() != null && profile.getProfilePicture().length > 0) {
      base64Picture = "data:image/png;base64,"
              + Base64.getEncoder().encodeToString(profile.getProfilePicture());
    }

    return CurrentUserResponseDTO.builder()
            .id(user.getId())
            .email(user.getEmail())
            .firstName(profile != null ? profile.getFirst_name() : null)
            .lastName(profile != null ? profile.getLast_name() : null)
            .alias(profile != null ? profile.getAlias() : null)
            .profilePicture(base64Picture)
            .role(user.getRoles())
            .build();
  }

  @Transactional
  public CurrentUserResponseDTO getCurrentUserDTO(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MESSAGE));
        UserProfile userProfile = user.getProfile();
        String base64Picture = null;
        if (userProfile != null && userProfile.getProfilePicture() != null
                && userProfile.getProfilePicture().length > 0) {
            base64Picture = "data:image/png;base64,"
                    + Base64.getEncoder().encodeToString(userProfile.getProfilePicture());
        }

        return CurrentUserResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(userProfile != null ? userProfile.getFirst_name() : null)
                .lastName(userProfile != null ? userProfile.getLast_name() : null)
                .alias(userProfile != null ? userProfile.getAlias() : null)
                .role(user.getRoles())
                .profilePicture(base64Picture)
                .build();
  }

  @Transactional
  public ProfileResponseDTO getUserProfileDTO(Long currentUserId, Long targetUserId) {
    validateUserAccess(currentUserId, targetUserId);

    UserProfile profile = userRepository.findById(targetUserId)
            .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MESSAGE))
            .getProfile();

    return ProfileResponseDTO.builder()
            .first_name(profile.getFirst_name())
            .last_name(profile.getLast_name())
            .city(profile.getCity())
            .build();
  }

  public BiographicalResponseDTO getBiographicalResponseDTO(Long currentUserId, Long targetUserId) {
      validateUserAccess(currentUserId, targetUserId);

      UserProfile profile = userRepository.findById(targetUserId)
              .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MESSAGE)).getProfile();
      if (profile == null)
          throw new EntityNotFoundException("Profile not found!");

      return BiographicalResponseDTO.builder()
              .gender_self(new GenderTypeDTO(profile.getAttributes().getGender().getId(),
                      profile.getAttributes().getGender().getName()))
              .gender_other(new GenderTypeDTO(profile.getPreferences().getGender().getId(),
                      profile.getPreferences().getGender().getName()))
              .hobbies(profile.getHobbies().stream().map(Hobby::getId)
                      .collect(Collectors.toSet()))
              .age_self(Period.between(profile.getAttributes().getBirth_date(), LocalDate.now())
                      .getYears())
              .age_min(profile.getPreferences().getAge_min())
              .age_max(profile.getPreferences().getAge_max())
              .distance(profile.getPreferences().getDistance())
              .probability_tolerance(profile.getPreferences().getProbability_tolerance()).build();
  }

  public SettingsResponseDTO getSettingsResponseDTO(Long currentUserId, Long targetUserId) {
    validateUserAccess(currentUserId, targetUserId);
    UserParametersResponseDTO parameters = getParameters(targetUserId);

    return SettingsResponseDTO.builder()
    .email(parameters.email())
    .number(parameters.number())
    .firstName(parameters.first_name())
    .lastName(parameters.last_name())
    .alias(parameters.alias())
    .hobbies(parameters.hobbies())
    .genderSelf(parameters.gender_self())
    .birthDate(parameters.birth_date())
    .city(parameters.city())
    .longitude(parameters.longitude())
    .latitude(parameters.latitude())
    .genderOther(parameters.gender_other())
    .ageMin(parameters.age_min())
    .ageMax(parameters.age_max())
    .distance(parameters.distance())
    .probabilityTolerance(parameters.probability_tolerance())
    .build();
  }
}
