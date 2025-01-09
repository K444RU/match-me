package com.matchme.srv.service;

import java.util.Base64;
import java.util.List;

import com.matchme.srv.dto.request.settings.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.matchme.srv.dto.request.*;
import com.matchme.srv.dto.response.*;
import com.matchme.srv.exception.DuplicateFieldException;
import com.matchme.srv.exception.ResourceNotFoundException;
import com.matchme.srv.mapper.AttributesMapper;
import com.matchme.srv.mapper.PreferencesMapper;
import com.matchme.srv.mapper.UserParametersMapper;
import com.matchme.srv.model.connection.Connection;
import com.matchme.srv.model.user.User;
import com.matchme.srv.model.user.UserAuth;
import com.matchme.srv.model.user.UserRoleType;
import com.matchme.srv.model.user.UserStateTypes;
import com.matchme.srv.model.user.activity.ActivityLog;
import com.matchme.srv.model.user.activity.ActivityLogType;
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
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final UserGenderTypeRepository genderRepository;
  private final UserRoleTypeRepository roleRepository;
  private final ConnectionRepository connectionRepository;

  private final AttributesMapper attributesMapper;
  private final PreferencesMapper preferencesMapper;
  private final UserParametersMapper parametersMapper;

  private final ActivityLogTypeRepository activityLogTypeRepository;
  private final AttributeChangeTypeRepository attributeChangeTypeRepository;
  private final PreferenceChangeTypeRepository preferenceChangeTypeRepository;
  private final ProfileChangeTypeRepository profileChangeTypeRepository;
  private final UserStateTypesRepository userStateTypesRepository;

  private final PasswordEncoder encoder;

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
    UserGenderType gender = genderRepository.findById(genderId)
        .orElseThrow(() -> new RuntimeException("Gender not found!"));
    return gender;
  }

  // Creates User entity and UserAuth entity for user, sends verification e-mail
  @Transactional
  public ActivityLog createUser(SignupRequestDTO signUpRequest) {
    System.out.println("Creating user with email: " + signUpRequest.getEmail());

    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      throw new DuplicateFieldException("email", "Email already exists");
    }

    if (userRepository.existsByNumber(signUpRequest.getNumber())) {
      throw new DuplicateFieldException("number", "Phone number already exists");
    }

    UserStateTypes state = userStateTypesRepository.findByName("UNVERIFIED")
        .orElseThrow(() -> new RuntimeException("UserState not found"));

    // Create new user (email, number, status) + state
    User newUser = new User(signUpRequest.getEmail(), signUpRequest.getNumber(), state);
    assignDefaultRole(newUser);

    // Create Auth entity for the user and assign it the given password encoded.
    UserAuth newAuth = new UserAuth(encoder.encode(signUpRequest.getPassword()));
    newUser.setUserAuth(newAuth);

    ActivityLogType logType = activityLogTypeRepository.findByName("CREATED")
        .orElseThrow(() -> new RuntimeException("LogType not found"));

    ActivityLog newEntry = new ActivityLog(newUser, logType);

    userRepository.save(newUser);

    System.out.println(newEntry);

    // TODO: Send email verification to email

    System.out.println("User created: " + newUser);
    return newEntry;
  }

  // Verifies e-mail, creates UserProfile, UserAttributes and UserPreferences
  // entities for the user
  public boolean verifyAccount(Long userId, int verificationCode) {

    // Get user entity
    User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
    UserAuth auth = user.getUserAuth();

    // Verify account
    if (auth.getRecovery() == verificationCode) {
      user.setState(userStateTypesRepository.findByName("VERIFIED")
          .orElseThrow(() -> new RuntimeException("UserState not found")));
      auth.setRecovery(null);
      ActivityLogType logType = activityLogTypeRepository.findByName("VERIFIED")
          .orElseThrow(() -> new RuntimeException("LogType not found"));
      System.out.println(new ActivityLog(user, logType));
    } else {
      throw new RuntimeException("Verification code was wrong! Would you like us to generate the code again?");
    }

    // Create profile entity for user
    UserProfile newProfile = new UserProfile();
    user.setProfile(newProfile);
    ProfileChangeType profileType = profileChangeTypeRepository.findByName("CREATED")
        .orElseThrow(() -> new RuntimeException("Profile Change Type not found"));
    System.out.println(new ProfileChange(newProfile, profileType, null));

    // Create attributes entity for user
    UserAttributes newAttributes = new UserAttributes();
    newProfile.setAttributes(newAttributes);
    AttributeChangeType attributeType = attributeChangeTypeRepository.findByName("CREATED")
        .orElseThrow(() -> new RuntimeException("Type not found"));
    System.out.println(new AttributeChange(newAttributes, attributeType, null));

    // Create preferences entity for user
    UserPreferences newPreferences = new UserPreferences();
    newProfile.setPreferences(newPreferences);
    PreferenceChangeType preferenceChangeType = preferenceChangeTypeRepository.findByName("CREATED")
        .orElseThrow(() -> new RuntimeException("Preference Change Type not found"));
    System.out.println(new PreferenceChange(newPreferences, preferenceChangeType, null));

    // Should cascade everything
    userRepository.save(user);

    return true;
  }

  // Finish setting up account data after verifying email.
  public ActivityLog setUserParameters(Long userId, UserParametersRequestDTO parameters) {

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("User not found!"));

    UserProfile profile = user.getProfile();
    if (profile == null) {
      profile = new UserProfile();
      user.setProfile(profile);

      ProfileChangeType profileType = profileChangeTypeRepository.findByName("CREATED")
          .orElseThrow(() -> new ResourceNotFoundException("Profile Change Type"));
      new ProfileChange(profile, profileType, null);
    }

    UserAttributes attributes = profile.getAttributes();
    if (attributes == null) {
      attributes = new UserAttributes();
      profile.setAttributes(attributes);

      AttributeChangeType attributeType = attributeChangeTypeRepository.findByName("CREATED")
          .orElseThrow(() -> new ResourceNotFoundException("Attribute Change Type"));
      new AttributeChange(attributes, attributeType, null);
    }

    UserPreferences preferences = profile.getPreferences();
    if (preferences == null) {
      preferences = new UserPreferences();
      profile.setPreferences(preferences);

      PreferenceChangeType preferenceChangeType = preferenceChangeTypeRepository.findByName("CREATED")
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

    user.setState(userStateTypesRepository.findByName("NEW")
        .orElseThrow(() -> new ResourceNotFoundException("User state")));

    ActivityLogType activitylogType = activityLogTypeRepository.findByName("VERIFIED")
        .orElseThrow(() -> new ResourceNotFoundException("LogType"));

    ActivityLog newEntry = new ActivityLog(user, activitylogType);

    // Now user can have a score entity and start looking for a chat
    UserScore score = new UserScore();
    user.setScore(score);

    userRepository.save(user);

    return newEntry;
  }

  public boolean updateAccountSettings(Long userId, AccountSettingsRequestDTO settings) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("User not found!"));

    user.setEmail(settings.getEmail());
    user.setNumber(settings.getNumber());

    // TODO: Add logging
    userRepository.save(user);
    return true;
  }

  public boolean updateProfileSettings(Long userId, ProfileSettingsRequestDTO settings) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("User not found!"));

    UserProfile profile = user.getProfile();
    profile.setFirst_name(settings.getFirst_name());
    profile.setLast_name(settings.getLast_name());
    profile.setAlias(settings.getAlias());

    // TODO: Add logging
    userRepository.save(user);
    return true;
  }

  public boolean updateAttributesSettings(Long userId, AttributesSettingsRequestDTO settings) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("User not found!"));

    UserProfile profile = user.getProfile();
    UserAttributes attributes = profile.getAttributes();

    attributesMapper.toEntity(attributes, settings);
    attributes.setGender(getGender(settings.getGender_self()));
    attributes.setLocation(List.of(settings.getLongitude(), settings.getLatitude()));

    profile.setCity(settings.getCity());

    // TODO: Add logging
    userRepository.save(user);
    return true;
  }

  public boolean updatePreferencesSettings(Long userId, PreferencesSettingsRequestDTO settings) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("User not found!"));

    UserProfile profile = user.getProfile();
    UserPreferences preferences = profile.getPreferences();

    preferencesMapper.toEntity(preferences, settings);
    preferences.setGender(getGender(settings.getGender_other()));

    // TODO: Add logging
    userRepository.save(user);
    return true;
  }

  public UserParametersResponseDTO getParameters(Long userId) {

    User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found!"));
    UserAuth auth = user.getUserAuth();
    UserProfile profile = user.getProfile();
    UserAttributes attributes = profile.getAttributes();
    UserPreferences preferences = profile.getPreferences();

    return parametersMapper.toUserParametersDTO(user, attributes, preferences, auth);
  }

  public UserProfile getUserProfile(Long userId) {
    User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found!"));
    return user.getProfile();
  }

  public User getUser(Long userId) {
    User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found!"));
    return user;
  }

  public User getUserByEmail(String email) {
    User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found!"));
    return user;
  }

  public void removeUserByEmail(String email) {
    User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found!"));
    userRepository.delete(user);
  }

  /**
   * Checks if two users have an established connection.
   * <p>
   * Currently DOES NOT account for inactive connections
   */
  public boolean isConnected(Long requesterId, Long targetId) {
    List<Connection> connections = connectionRepository.findConnectionsByUserId(requesterId);
    for (Connection connection : connections) {
      if (connection.getUsers().stream().anyMatch(user -> user.getId().equals(targetId)))
        return true;
    }
    return false;
  }

  @Transactional
  public void saveProfilePicture(Long userId, ProfilePictureSettingsRequestDTO request) {

    validateProfilePictureRequest(request);

    String base64Part = extractBase64Part(request.getBase64Image());
    byte[] imageBytes = decodeBase64Image(base64Part);

    User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found for ID: " + userId));

    UserProfile profile = user.getProfile();
    if (profile == null) {
      profile = new UserProfile();
      user.setProfile(profile);
    }

    profile.setProfilePicture(imageBytes);
    userRepository.save(user);
  }

  private void validateProfilePictureRequest(ProfilePictureSettingsRequestDTO request) {
    if (request == null || request.getBase64Image() == null || request.getBase64Image().isEmpty()) {
      throw new IllegalArgumentException("Invalid base64 image data in the request.");
    }
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

  /**
   * Validate that `currentUserId` can view `targetUserId`.
   * If not, throw an exception (404 or 403).
   */
  public void validateUserAccess(Long currentUserId, Long targetUserId) {
    if (!currentUserId.equals(targetUserId) && !isConnected(currentUserId, targetUserId)) {
      throw new EntityNotFoundException("User not found or no access rights.");
    }
  }

  /**
   * Return a user’s basic info,
   * first checks whether the current user can access the target’s data.
   */
  @Transactional(readOnly = true)
  public CurrentUserResponseDTO getUserDTO(Long currentUserId, Long targetUserId) {
    validateUserAccess(currentUserId, targetUserId);
    User user = userRepository.findById(targetUserId)
            .orElseThrow(() -> new EntityNotFoundException("User not found!"));
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
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found!"));
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
            .orElseThrow(() -> new EntityNotFoundException("User not found!"))
            .getProfile();

    return ProfileResponseDTO.builder()
            .first_name(profile.getFirst_name())
            .last_name(profile.getLast_name())
            .city(profile.getCity())
            .build();
  }
}

  // public void setAttributes(Long userId) {

  // UserAttributes attributes = attributesRepository.findById(userId)
  // .orElseThrow(() -> new EntityNotFoundException("UserAttributes not found!"));

  // attributesRepository.save(attributes);
  // }

  // public SettingsResponseDTO getSettings(Long userId) {

  // User user = userRepository.findById(userId).orElseThrow(() -> new
  // EntityNotFoundException("User not found!"));
  // UserAuth auth = user.getUserAuth();

  // return new SettingsResponseDTO(user.getEmail(), user.getNumber(),
  // auth.getPassword());
  // }

  // public AttributesResponseDTO getAttributes(Long userId) {

  // UserAttributes attributes = attributesRepository.findById(userId)
  // .orElseThrow(() -> new EntityNotFoundException("UserAttributes not found!"));

  // return new AttributesResponseDTO(attributes.getGender(),
  // attributes.getBirthDate(), attributes.getLocation());
  // }

  // public PreferencesResponseDTO getPreferences(Long userId) {

  // UserPreferences preferences = preferencesRepository.findById(userId)
  // .orElseThrow(() -> new EntityNotFoundException("UserPreferences not
  // found!"));

  // return new PreferencesResponseDTO(preferences.getGender(),
  // preferences.getAge_min(), preferences.getAge_max(),
  // preferences.getDistance());
  // }

  // public ProfileResponseDTO getProfile(Long userId) {

  // UserProfile profile = profileRepository.findById(userId)
  // .orElseThrow(() -> new EntityNotFoundException("UserProfile not found!"));

  // return new ProfileResponseDTO(profile.getFirstName(), profile.getLastName());
  // }
