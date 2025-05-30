package com.matchme.srv.service.user;

import com.matchme.srv.dto.request.SignupRequestDTO;
import com.matchme.srv.dto.request.UserParametersRequestDTO;
import com.matchme.srv.exception.InvalidVerificationException;
import com.matchme.srv.mapper.AttributesMapper;
import com.matchme.srv.mapper.PreferencesMapper;
import com.matchme.srv.model.enums.UserState;
import com.matchme.srv.model.user.User;
import com.matchme.srv.model.user.UserAuth;
import com.matchme.srv.model.user.UserRoleType;
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
import com.matchme.srv.service.type.*;
import com.matchme.srv.service.user.validation.UserValidationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCreationService {

    private final UserRepository userRepository;

    private final UserRoleTypeService userRoleTypeService;
    private final ActivityLogTypeService activityLogTypeService;
    private final ProfileChangeTypeService profileChangeTypeService;
    private final AttributeChangeTypeService attributeChangeTypeService;
    private final PreferenceChangeTypeService preferenceChangeTypeService;
    private final HobbyService hobbyService;
    private final UserValidationService userValidationService;

    private final PasswordEncoder encoder;

    private final AttributesMapper attributesMapper;
    private final PreferencesMapper preferencesMapper;

    private static final String USER_NOT_FOUND_MESSAGE = "User not found!";
    private static final String CREATED = "CREATED";

    // Creates User entity and UserAuth entity for user, sends verification e-mail
    @Transactional
    public ActivityLog createUser(SignupRequestDTO signUpRequest) {
        log.info("Creating user with email: {}", signUpRequest.getEmail());

        userValidationService.validateUniqueEmailAndNumber(signUpRequest.getEmail(), signUpRequest.getNumber(), null);
      
        // UserState state = UserState.UNVERIFIED;
        UserState state = UserState.PROFILE_INCOMPLETE;

        // Create new user (email, number, status)
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
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MESSAGE));
        UserAuth auth = user.getUserAuth();

        // Verify account
        if (auth.getRecovery() != null && auth.getRecovery().equals(verificationCode)) {
        	// Set state to PROFILE_INCOMPLETE after email verification
        	user.setState(UserState.PROFILE_INCOMPLETE);
            auth.setRecovery(null);
            // Log type might need adjustment if "VERIFIED" log type implies full activation
            ActivityLogType logType = activityLogTypeService.getByName("VERIFIED"); // Assuming VERIFIED log type is still relevant
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

    @Transactional
    public ActivityLog setUserParameters(Long userId, UserParametersRequestDTO parameters) {

        User user = userRepository
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
        attributes.setGender(parameters.gender_self());

        // Add null check for location coordinates
        if (parameters.longitude() == null || parameters.latitude() == null) {
            throw new IllegalArgumentException("Longitude and latitude must be provided");
        }
        attributes.setLocation(List.of(parameters.longitude(), parameters.latitude()));

        preferencesMapper.toEntity(preferences, parameters);
        preferences.setGender(parameters.gender_other());

        profile.setFirst_name(parameters.first_name());
        profile.setLast_name(parameters.last_name());
        profile.setAlias(parameters.alias());
        profile.setCity(parameters.city());
        profile.setAboutMe(parameters.aboutMe());

        if (parameters.hobbies() != null && !parameters.hobbies().isEmpty()) {
            Set<Hobby> foundHobbies = parameters.hobbies().stream().map(hobbyService::getById)
                    .collect(Collectors.toSet());
            profile.setHobbies(foundHobbies);
        } else {
            profile.setHobbies(new HashSet<>());
        }
      
        // Set state to PROFILE_INCOMPLETE after initial parameters are set
        // The transition to ACTIVE will happen in UserSettingsService when all required fields are confirmed
        user.setState(UserState.PROFILE_INCOMPLETE);

        log.info("User state set to PROFILE_INCOMPLETE");

        // Log type might need adjustment here too. Maybe a "PROFILE_UPDATED" type?
        ActivityLogType activitylogType = activityLogTypeService.getByName("VERIFIED"); // Reusing VERIFIED for now
      
        ActivityLog newEntry = new ActivityLog(user, activitylogType);

        // Now user can have a score entity and start looking for a chat
        if (user.getScore() == null) {
            user.setScore(new UserScore(user));
        }


        log.info("Setting user parameters for user ID: {}", user.getId());
        User savedUser = userRepository.save(user);
        log.info("User parameters set {}", savedUser);

        checkAndActivateProfile(savedUser);

        return newEntry;
    }

    public void assignDefaultRole(User user) {
        UserRoleType defaultRole = userRoleTypeService.getByName("ROLE_USER");
        user.setRole(defaultRole);
    }

    public void removeUserByEmail(String email) {
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MESSAGE));
        userRepository.delete(user);
    }

    private void checkAndActivateProfile(User user) {
        if (user == null || user.getState() != UserState.PROFILE_INCOMPLETE) {
            // Only proceed if the user exists and is currently in PROFILE_INCOMPLETE state
            return;
     }
   
     UserProfile profile = user.getProfile();
     if (profile == null) {
         log.warn("User ID: {} is PROFILE_INCOMPLETE but has no UserProfile.", user.getId());
         return;
     }
   
     UserAttributes attributes = profile.getAttributes();
     if (attributes == null) {
         log.warn("User ID: {} is PROFILE_INCOMPLETE but has no UserAttributes.", user.getId());
         return;
     }
   
     // Check if all required fields are populated
     boolean firstNamePresent = StringUtils.hasText(profile.getFirst_name());
     boolean lastNamePresent = StringUtils.hasText(profile.getLast_name());
     boolean aliasPresent = StringUtils.hasText(profile.getAlias());
     boolean cityPresent = StringUtils.hasText(profile.getCity());
     boolean genderPresent = attributes.getGender() != null;
     boolean birthdatePresent = attributes.getBirthdate() != null;
     boolean locationPresent = attributes.getLocation() != null && attributes.getLocation().size() == 2 &&
             attributes.getLocation().stream().allMatch(Objects::nonNull);
   
     if (firstNamePresent && lastNamePresent && aliasPresent && cityPresent &&
             genderPresent && birthdatePresent && locationPresent) {
   
         log.info("All required profile fields present for user ID: {}. Attempting to activate profile.", user.getId());
         user.setState(UserState.ACTIVE);
         log.info("User ID: {} state changed from {} to {}.", user.getId(), UserState.PROFILE_INCOMPLETE, UserState.ACTIVE);
     } else {
         log.debug("User ID: {} profile still incomplete. State remains {}.", user.getId(), UserState.PROFILE_INCOMPLETE);
         }
     }
}
