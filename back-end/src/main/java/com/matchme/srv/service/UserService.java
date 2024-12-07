package com.matchme.srv.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.matchme.srv.dto.request.*;
import com.matchme.srv.dto.response.*;
import com.matchme.srv.mapper.AttributesMapper;
import com.matchme.srv.mapper.PreferencesMapper;
import com.matchme.srv.mapper.UserParametersMapper;
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
import com.matchme.srv.model.user.profile.user_score.UserScore;
import com.matchme.srv.repository.*;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final UserProfileRepository profileRepository;
  private final UserAttributesRepository attributesRepository;
  private final UserPreferencesRepository preferencesRepository;
  private final UserAuthRepository authRepository;
  private final AttributesMapper attributesMapper;
  private final PreferencesMapper preferencesMapper;
  private final UserScoreRepository scoreRepository;
  private final UserParametersMapper parametersMapper;
  private final PasswordEncoder encoder;

  // Creates User entity and UserAuth entity for user, sends verification e-mail, returns e-mail
  public String createUser(SignupRequestDTO signUpRequest) {
    
    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      throw new RuntimeException("Error: Email is already in use!");
    }

    // Create new user (email, status and role)
    User newUser = new User(signUpRequest.getEmail());

    // Create Auth entity for the user and assign it the given password encoded.
    UserAuth newAuth = new UserAuth(encoder.encode(signUpRequest.getPassword()));
    newUser.setUserAuth(newAuth);

    System.out.println(new ActivityLog(newUser, ActivityLog.LogType.CREATED));
    
    userRepository.save(newUser);

    //TODO: Send email verification to email

    return newUser.getEmail();
  }

  // Verifies e-mail, creates UserProfile, UserAttributes and UserPreferences entities for the user
  public boolean verifyAccount(Long userId, int verificationCode) {

    // Get user entity
    User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
    UserAuth auth = user.getUserAuth(); // Load userAuth - (LAZY loading)
    
    // Verify account 
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
  public User finishSettingUpAccount(Long userId, UserParametersRequestDTO parameters) {

    User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found!"));
    UserProfile profile = user.getProfile();
    user.setNumber(parameters.number());

    UserAttributes attributes = profile.getAttributes();
    attributesMapper.toEntity(attributes, parameters);

    UserPreferences preferences = profile.getPreferences();
    preferencesMapper.toEntity(preferences, parameters);

    UserScore newScore = new UserScore();
    user.setScore(newScore);

    user.setState(UserState.NEW);
    System.out.println(new ActivityLog(user, ActivityLog.LogType.STATE_NEW));

    userRepository.save(user);
    
    return user;
  }

  public ProfileResponseDTO getProfile(Long userId) {

    UserProfile profile = profileRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("UserProfile not found!"));

    return new ProfileResponseDTO(profile.getFirstName(), profile.getLastName());
  }

  public UserParametersResponseDTO getParameters(Long userId) {

    User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found!"));
    UserAuth auth = user.getUserAuth();
    UserProfile profile = user.getProfile();
    UserAttributes attributes = profile.getAttributes();
    UserPreferences preferences = profile.getPreferences();

    return parametersMapper.toUserParametersDTO(user, attributes, preferences, auth);
  }
}