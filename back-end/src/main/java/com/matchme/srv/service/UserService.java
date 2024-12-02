package com.matchme.srv.service;

import java.time.Instant;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.matchme.srv.dto.request.SignupRequestDTO;
import com.matchme.srv.dto.response.MessageResponseDTO;
import com.matchme.srv.dto.response.SettingsResponseDTO;
import com.matchme.srv.model.user.Role;
import com.matchme.srv.model.user.User;
import com.matchme.srv.model.user.UserAuth;
import com.matchme.srv.model.user.UserState;
import com.matchme.srv.model.user.activity.ActivityLog;
import com.matchme.srv.model.user.profile.UserProfile;
import com.matchme.srv.model.user.profile.user_attributes.UserAttributes;
import com.matchme.srv.model.user.profile.user_preferences.UserPreferences;
import com.matchme.srv.repository.ActivityLogRepository;
import com.matchme.srv.repository.RoleRepository;
import com.matchme.srv.repository.UserAttributesRepository;
import com.matchme.srv.repository.UserAuthRepository;
import com.matchme.srv.repository.UserPreferencesRepository;
import com.matchme.srv.repository.UserProfileRepository;
import com.matchme.srv.repository.UserRepository;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final UserProfileRepository profileRepository;
  private final UserAttributesRepository attributesRepository;
  private final UserPreferencesRepository preferencesRepository;
  private final ActivityLogRepository activityRepository;
  private final RoleRepository roleRepository;
  private final UserAuthRepository authRepository;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  public UserService(UserRepository userRepository, UserProfileRepository userProfileRepository, UserAttributesRepository userAttributesRepository, UserPreferencesRepository userPreferencesRepository, ActivityLogRepository activityLogRepository, RoleRepository roleRepository, UserAuthRepository userAuthRepository) {
    this.userRepository = userRepository;
    this.profileRepository = userProfileRepository;
    this.attributesRepository = userAttributesRepository;
    this.preferencesRepository = userPreferencesRepository;
    this.activityRepository = activityLogRepository;
    this.roleRepository = roleRepository;
    this.authRepository = userAuthRepository;
  }

  public ActivityLog createUser(SignupRequestDTO signUpRequest) {
    
    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      throw new RuntimeException("Error: Email is already in use!");
    }

    // Create new user (email, status and role)
    User newUser = new User(signUpRequest.getEmail());

    // Create Auth entity for the user and assign it the given password encoded.
    UserAuth newAuth = new UserAuth(encoder.encode(signUpRequest.getPassword()));
    newUser.setUserAuth(newAuth);

    User user = userRepository.save(newUser);

    ActivityLog newEntry = new ActivityLog(user, ActivityLog.LogType.CREATED, Instant.now());

    System.out.println(newEntry);

    //Send email verification to email

    return activityRepository.save(newEntry);
  }

  // Finish setting up account data after verifying email. 
  public ActivityLog finishSettingUpAccount(Long userId, String number, String password) {

    Optional<User> possibleUser = userRepository.findById(userId);
    if (!possibleUser.isPresent()) {
      // TODO: Do something here, throw an exception...
    }
    User user = possibleUser.get();

    user.setNumber(number);
    //user.setPassword(password);
    user.setState(UserState.NEW);

    // Create a new profile and set it to user.
    UserProfile newProfile = new UserProfile();
    user.setProfile(newProfile);

    // Create attributes and preferences entities and set them to profile.
    UserAttributes newAttributes = new UserAttributes();
    newProfile.setAttributes(newAttributes);

    UserPreferences newPreferences = new UserPreferences();
    newProfile.setPreferences(newPreferences);

    userRepository.save(user);

    ActivityLog newEntry = new ActivityLog(user, ActivityLog.LogType.VERIFIED, Instant.now());

    return activityRepository.save(newEntry);
  }

  public void setAttributes(Long userId) {
    Optional<UserAttributes> possibleAttributes = attributesRepository.findById(userId);

    if (!possibleAttributes.isPresent()) {
      // TODO: Do something, exception handler
    }

    UserAttributes attributes = possibleAttributes.get();


  }

  public SettingsResponseDTO getSettings(Long userId) {
    
    Optional<User> possibleUser = userRepository.findById(userId);
    if (!possibleUser.isPresent()) {
      // TODO: Throw error
    }
    User user = possibleUser.get();

    Optional<UserAuth> possibleAuth = authRepository.findById(userId);
    if (!possibleAuth.isPresent()) {
      // TODO: Throw error
    }
    UserAuth auth = possibleAuth.get();

    return new SettingsResponseDTO(user.getEmail(), user.getNumber(), auth.getPassword());
  }

}