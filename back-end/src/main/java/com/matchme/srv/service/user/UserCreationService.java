package com.matchme.srv.service.user;

import com.matchme.srv.dto.request.SignupRequestDTO;
import com.matchme.srv.dto.request.UserParametersRequestDTO;
import com.matchme.srv.exception.InvalidVerificationException;
import com.matchme.srv.mapper.AttributesMapper;
import com.matchme.srv.mapper.PreferencesMapper;
import com.matchme.srv.model.user.User;
import com.matchme.srv.model.user.UserAuth;
import com.matchme.srv.model.user.UserRoleType;
import com.matchme.srv.model.user.UserStateTypes;
import com.matchme.srv.model.user.activity.ActivityLog;
import com.matchme.srv.model.user.activity.ActivityLogType;
import com.matchme.srv.model.user.profile.Hobby;
import com.matchme.srv.model.user.profile.ProfileChange;
import com.matchme.srv.model.user.profile.ProfileChangeType;
import com.matchme.srv.model.user.profile.UserProfile;
import com.matchme.srv.model.user.profile.user_attributes.AttributeChange;
import com.matchme.srv.model.user.profile.user_attributes.AttributeChangeType;
import com.matchme.srv.model.user.profile.user_attributes.UserAttributes;
import com.matchme.srv.model.user.profile.user_preferences.PreferenceChange;
import com.matchme.srv.model.user.profile.user_preferences.PreferenceChangeType;
import com.matchme.srv.model.user.profile.user_preferences.UserPreferences;
import com.matchme.srv.model.user.profile.user_score.UserScore;
import com.matchme.srv.repository.UserRepository;
import com.matchme.srv.service.HobbyService;
import com.matchme.srv.service.type.ActivityLogTypeService;
import com.matchme.srv.service.type.AttributeChangeTypeService;
import com.matchme.srv.service.type.PreferenceChangeTypeService;
import com.matchme.srv.service.type.ProfileChangeTypeService;
import com.matchme.srv.service.type.UserGenderTypeService;
import com.matchme.srv.service.type.UserRoleTypeService;
import com.matchme.srv.service.type.UserStateTypesService;
import com.matchme.srv.service.user.validation.UserValidationService;

import jakarta.persistence.EntityNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCreationService {

  private final UserRepository userRepository;

  private final UserRoleTypeService userRoleTypeService;
  private final UserStateTypesService userStateTypesService;
  private final ActivityLogTypeService activityLogTypeService;
  private final ProfileChangeTypeService profileChangeTypeService;
  private final AttributeChangeTypeService attributeChangeTypeService;
  private final PreferenceChangeTypeService preferenceChangeTypeService;
  private final HobbyService hobbyService;
  private final UserGenderTypeService userGenderTypeService;
  private final UserValidationService userValidationService;

  private final PasswordEncoder encoder;

  private final AttributesMapper attributesMapper;
  private final PreferencesMapper preferencesMapper;

  private static final String USER_NOT_FOUND_MESSAGE = "User not found!";
  private static final String VERIFIED = "VERIFIED";
  private static final String CREATED = "CREATED";

  // Creates User entity and UserAuth entity for user, sends verification e-mail
  @Transactional
  public ActivityLog createUser(SignupRequestDTO signUpRequest) {
    log.info("Creating user with email: {}", signUpRequest.getEmail());

    userValidationService.validateUniqueEmailAndNumber(signUpRequest.getEmail(), signUpRequest.getNumber(), null);

    UserStateTypes state = userStateTypesService.getByName("UNVERIFIED");

    // Create new user (email, number, status) + state
    User newUser = new User(signUpRequest.getEmail(), signUpRequest.getNumber(), state);
    assignDefaultRole(newUser);

    // Create Auth entity for the user and assign it the given password encoded.
    UserAuth newAuth = new UserAuth(encoder.encode(signUpRequest.getPassword()));
    newUser.setUserAuth(newAuth);

    ActivityLogType logType = activityLogTypeService.getByName(CREATED);

    ActivityLog newEntry = new ActivityLog(newUser, logType);

    userRepository.save(newUser);

    log.info(newEntry.toString());

    // TODO: Send email verification to email

    log.info("User created: {}", newUser);
    return newEntry;
  }

  // Verifies e-mail, creates UserProfile, UserAttributes and UserPreferences
  // entities for the user
  public void verifyAccount(Long userId, int verificationCode) {

    // Get user entity
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MESSAGE));
    UserAuth auth = user.getUserAuth();

    // Verify account
    if (auth.getRecovery() != null && auth.getRecovery().equals(verificationCode)) {
      user.setState(userStateTypesService.getByName(VERIFIED));
      auth.setRecovery(null);
      ActivityLogType logType = activityLogTypeService.getByName(VERIFIED);
      log.info(new ActivityLog(user, logType).toString());
    } else {
      throw new InvalidVerificationException(
          "Verification code was wrong! Would you like us to generate the code again?");
    }

    // Create profile entity for user
    UserProfile newProfile = new UserProfile();
    user.setProfile(newProfile);
    ProfileChangeType profileType = profileChangeTypeService.getByName(CREATED);
    log.info(new ProfileChange(newProfile, profileType, null).toString());

    // Create attributes entity for user
    UserAttributes newAttributes = new UserAttributes();
    newProfile.setAttributes(newAttributes);
    AttributeChangeType attributeType = attributeChangeTypeService.getByName(CREATED);
    log.info(new AttributeChange(newAttributes, attributeType, null).toString());

    // Create preferences entity for user
    UserPreferences newPreferences = new UserPreferences();
    newProfile.setPreferences(newPreferences);
    PreferenceChangeType preferenceChangeType = preferenceChangeTypeService.getByName(CREATED);
    log.info(new PreferenceChange(newPreferences, preferenceChangeType, null).toString());

    // Should cascade everything
    userRepository.save(user);
  }

  public ActivityLog setUserParameters(Long userId, UserParametersRequestDTO parameters) {

    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MESSAGE));

    UserProfile profile = user.getProfile();
    if (profile == null) {
      profile = new UserProfile();
      user.setProfile(profile);

      ProfileChangeType profileType = profileChangeTypeService.getByName(CREATED);
      new ProfileChange(profile, profileType, null);
    }

    UserAttributes attributes = profile.getAttributes();
    if (attributes == null) {
      attributes = new UserAttributes();
      profile.setAttributes(attributes);

      AttributeChangeType attributeType = attributeChangeTypeService.getByName(CREATED);
      new AttributeChange(attributes, attributeType, null);
    }

    UserPreferences preferences = profile.getPreferences();
    if (preferences == null) {
      preferences = new UserPreferences();
      profile.setPreferences(preferences);

      PreferenceChangeType preferenceChangeType = preferenceChangeTypeService.getByName(CREATED);
      new PreferenceChange(preferences, preferenceChangeType, null);
    }

    attributesMapper.toEntity(attributes, parameters);
    attributes.setGender(userGenderTypeService.getById(parameters.gender_self()));

    // Add null check for location coordinates
    if (parameters.longitude() == null || parameters.latitude() == null) {
      throw new IllegalArgumentException("Longitude and latitude must be provided");
    }
    attributes.setLocation(List.of(parameters.longitude(), parameters.latitude()));

    preferencesMapper.toEntity(preferences, parameters);
    preferences.setGender(userGenderTypeService.getById(parameters.gender_other()));

    profile.setFirst_name(parameters.first_name());
    profile.setLast_name(parameters.last_name());
    profile.setAlias(parameters.alias());
    profile.setCity(parameters.city());

    if (parameters.hobbies() != null && !parameters.hobbies().isEmpty()) {
      Set<Hobby> foundHobbies =
          parameters.hobbies().stream().map(hobbyService::getById).collect(Collectors.toSet());
      profile.setHobbies(foundHobbies);
    } else {
      profile.setHobbies(new HashSet<>());
    }

    user.setState(userStateTypesService.getByName("NEW"));

    ActivityLogType activitylogType = activityLogTypeService.getByName(VERIFIED);

    ActivityLog newEntry = new ActivityLog(user, activitylogType);

    // Now user can have a score entity and start looking for a chat
    UserScore score = new UserScore();
    user.setScore(score);

    userRepository.save(user);

    return newEntry;
  }

  public void assignDefaultRole(User user) {
    UserRoleType defaultRole = userRoleTypeService.getByName("ROLE_USER");
    user.setRole(defaultRole);
  }

  public void removeUserByEmail(String email) {
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MESSAGE));
    userRepository.delete(user);
  }
}
