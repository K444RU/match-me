package com.matchme.srv.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.matchme.srv.dto.request.*;
import com.matchme.srv.dto.response.*;
import com.matchme.srv.exception.DuplicateFieldException;
import com.matchme.srv.mapper.AttributesMapper;
import com.matchme.srv.mapper.PreferencesMapper;
import com.matchme.srv.mapper.UserParametersMapper;
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

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final UserGenderTypeRepository genderRepository;
  private final UserRoleTypeRepository roleRepository;

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
    UserGenderType gender = genderRepository.findById(genderId).orElseThrow(() -> new RuntimeException("Gender not found!"));
    return gender;
  }

  // Creates User entity and UserAuth entity for user, sends verification e-mail
  public ActivityLog createUser(SignupRequestDTO signUpRequest) {

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

    UserAttributes attributes = profile.getAttributes();
    attributesMapper.toEntity(attributes, parameters);
    attributes.setGender(getGender(parameters.gender_self()));

    UserPreferences preferences = profile.getPreferences();
    preferencesMapper.toEntity(preferences, parameters);
    preferences.setGender(getGender(parameters.gender_other()));

    user.setState(userStateTypesRepository.findByName("NEW")
      .orElseThrow(() -> new RuntimeException("User state not found")));

    ActivityLogType activitylogType = activityLogTypeRepository.findByName("VERIFIED")
      .orElseThrow(() -> new RuntimeException("LogType not found"));

    ActivityLog newEntry = new ActivityLog(user, activitylogType);

    // Now user can have a score entity and start looking for a chat
    UserScore score = new UserScore();
    user.setScore(score);

    userRepository.save(user);

    return newEntry;
  }

  public UserParametersResponseDTO getParameters(Long userId) {

    User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found!"));
    UserAuth auth = user.getUserAuth();
    UserProfile profile = user.getProfile();
    UserAttributes attributes = profile.getAttributes();
    UserPreferences preferences = profile.getPreferences();

    return parametersMapper.toUserParametersDTO(user, attributes, preferences, auth);
  }
  

  // public void setAttributes(Long userId) {

  //   UserAttributes attributes = attributesRepository.findById(userId)
  //       .orElseThrow(() -> new EntityNotFoundException("UserAttributes not found!"));

  //   attributesRepository.save(attributes);
  // }

  // public SettingsResponseDTO getSettings(Long userId) {

  //   User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found!"));
  //   UserAuth auth = user.getUserAuth();

  //   return new SettingsResponseDTO(user.getEmail(), user.getNumber(), auth.getPassword());
  // }

  // public AttributesResponseDTO getAttributes(Long userId) {

  //   UserAttributes attributes = attributesRepository.findById(userId)
  //       .orElseThrow(() -> new EntityNotFoundException("UserAttributes not found!"));

  //   return new AttributesResponseDTO(attributes.getGender(), attributes.getBirthDate(), attributes.getLocation());
  // }

  // public PreferencesResponseDTO getPreferences(Long userId) {

  //   UserPreferences preferences = preferencesRepository.findById(userId)
  //       .orElseThrow(() -> new EntityNotFoundException("UserPreferences not found!"));

  //   return new PreferencesResponseDTO(preferences.getGender(), preferences.getAge_min(), preferences.getAge_max(),
  //       preferences.getDistance());
  // }

  // public ProfileResponseDTO getProfile(Long userId) {

  //   UserProfile profile = profileRepository.findById(userId)
  //       .orElseThrow(() -> new EntityNotFoundException("UserProfile not found!"));

  //   return new ProfileResponseDTO(profile.getFirstName(), profile.getLastName());
  // }
}