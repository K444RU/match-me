package com.matchme.srv.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.matchme.srv.dto.request.*;
import com.matchme.srv.dto.response.*;

import com.matchme.srv.model.user.User;
import com.matchme.srv.model.user.UserAuth;
import com.matchme.srv.model.user.UserState;
import com.matchme.srv.model.user.activity.ActivityLog;
import com.matchme.srv.model.user.activity.ActivityLog.LogType;
import com.matchme.srv.model.user.profile.ProfileChange;
import com.matchme.srv.model.user.profile.UserProfile;
import com.matchme.srv.model.user.profile.user_attributes.AttributeChange;
import com.matchme.srv.model.user.profile.user_attributes.UserAttributes;
import com.matchme.srv.model.user.profile.user_preferences.PreferenceChange;
import com.matchme.srv.model.user.profile.user_preferences.UserPreferences;

import com.matchme.srv.repository.*;

import jakarta.persistence.EntityNotFoundException;

@Service
// @RequiredArgsConstructor - replaces the constructor?
public class UserService {

  private final UserRepository userRepository;
  private final UserProfileRepository profileRepository;
  private final UserAttributesRepository attributesRepository;
  private final UserPreferencesRepository preferencesRepository;
  private final UserAuthRepository authRepository;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  public UserService(UserRepository userRepository, UserProfileRepository userProfileRepository, UserAttributesRepository userAttributesRepository, UserPreferencesRepository userPreferencesRepository, UserAuthRepository userAuthRepository) {
    this.userRepository = userRepository;
    this.profileRepository = userProfileRepository;
    this.attributesRepository = userAttributesRepository;
    this.preferencesRepository = userPreferencesRepository;
    this.authRepository = userAuthRepository;
  }

  // Creates User entity and UserAuth entity for user, sends verification e-mail
  public ActivityLog createUser(SignupRequestDTO signUpRequest) {
    
    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      throw new RuntimeException("Error: Email is already in use!");
    }

    // Create new user (email, status and role)
    User newUser = new User(signUpRequest.getEmail());

    // Create Auth entity for the user and assign it the given password encoded.
    UserAuth newAuth = new UserAuth(encoder.encode(signUpRequest.getPassword()));
    newUser.setUserAuth(newAuth);

    ActivityLog newEntry = new ActivityLog(newUser, ActivityLog.LogType.CREATED);
    
    userRepository.save(newUser);
    
    System.out.println(newEntry);

    //TODO: Send email verification to email


    return newEntry;
  }

  // Verifies e-mail, creates UserProfile, UserAttributes and UserPreferences entities for the user
  public boolean verifyAccount(Long userId, int verificationCode) {

    // Get user entity
    User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));

    // Verify account 
    UserAuth auth = user.getUserAuth();
    
    if (auth.getRecovery() == verificationCode) {
      user.setState(UserState.VERIFIED);
      auth.setRecovery(null);
      System.out.println(new ActivityLog(user, LogType.VERIFIED));
    } else {
      throw new RuntimeException("Verification code was wrong! Would you like us to generate the code again?");
    }

    // Create profile entity for user
    UserProfile newProfile = new UserProfile();
    user.setProfile(newProfile);
    System.out.println(new ProfileChange(newProfile, ProfileChange.ProfileChangeType.CREATED, null));

    // Create attributes entity for user
    UserAttributes newAttributes = new UserAttributes();
    newProfile.setAttributes(newAttributes);
    System.out.println(new AttributeChange(newAttributes, AttributeChange.AttributeChangeType.CREATED, null));

    // Create preferences entity for user
    UserPreferences newPreferences = new UserPreferences();
    newProfile.setPreferences(newPreferences);
    System.out.println(new PreferenceChange(newPreferences, PreferenceChange.PreferenceChangeType.CREATED, null));

    // Should cascade everything
    userRepository.save(user);

    return true;
  }

  // Finish setting up account data after verifying email. 
  public ActivityLog finishSettingUpAccount(Long userId, String number, String password) {

    User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found!"));

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

    ActivityLog newEntry = new ActivityLog(user, ActivityLog.LogType.VERIFIED);

    userRepository.save(user);
    
    return newEntry;
  }

  public void setAttributes(Long userId) {

    UserAttributes attributes = attributesRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("UserAttributes not found!"));

    attributesRepository.save(attributes);
  }

  public SettingsResponseDTO getSettings(Long userId) {
    
    User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found!"));
    UserAuth auth = user.getUserAuth();

    return new SettingsResponseDTO(user.getEmail(), user.getNumber(), auth.getPassword());
  }

  public AttributesResponseDTO getAttributes(Long userId) {

    UserAttributes attributes = attributesRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("UserAttributes not found!"));

    return new AttributesResponseDTO(attributes.getGender(), attributes.getBirthDate(), attributes.getLocation());
  }

  public PreferencesResponseDTO getPreferences(Long userId) {

    UserPreferences preferences = preferencesRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("UserPreferences not found!"));

    return new PreferencesResponseDTO(preferences.getGender(), preferences.getAge_min(), preferences.getAge_max(), preferences.getDistance());
  }

  public ProfileResponseDTO getProfile(Long userId) {

    UserProfile profile = profileRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("UserProfile not found!"));

    return new ProfileResponseDTO(profile.getFirstName(), profile.getLastName());
  }

}