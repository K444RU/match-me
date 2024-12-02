package com.matchme.srv.service;

import java.time.Instant;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.matchme.srv.model.user.Role;
import com.matchme.srv.model.user.User;
import com.matchme.srv.model.user.UserState;
import com.matchme.srv.model.user.activity.ActivityLog;
import com.matchme.srv.model.user.profile.UserProfile;
import com.matchme.srv.model.user.profile.user_attributes.UserAttributes;
import com.matchme.srv.model.user.profile.user_preferences.UserPreferences;
import com.matchme.srv.repository.ActivityLogRepository;
import com.matchme.srv.repository.RoleRepository;
import com.matchme.srv.repository.UserAttributesRepository;
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

  @Autowired
  public UserService(UserRepository userRepository, UserProfileRepository userProfileRepository, UserAttributesRepository userAttributesRepository, UserPreferencesRepository userPreferencesRepository, ActivityLogRepository activityLogRepository, RoleRepository roleRepository) {
    this.userRepository = userRepository;
    this.profileRepository = userProfileRepository;
    this.attributesRepository = userAttributesRepository;
    this.preferencesRepository = userPreferencesRepository;
    this.activityRepository = activityLogRepository;
    this.roleRepository = roleRepository;
  }

  public ActivityLog createUser(String email) {
    
    // Create a new user and their profile
    User newUser = new User();

    // Set registration email as User email
    newUser.setEmail(email);

    // Set role as ROLE_USER
    Role userRole = roleRepository.findByName(Role.UserRole.ROLE_USER)
      .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
    
    newUser.setRole(userRole);

    // Set state to UNVERIFIED;
    newUser.setState(UserState.UNVERIFIED);

    User user = userRepository.save(newUser);

    ActivityLog newEntry = new ActivityLog(user, ActivityLog.LogType.CREATED, Instant.now());

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
    user.setPassword(password);
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


}