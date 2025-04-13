package com.matchme.srv.service.user;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.matchme.srv.dto.request.settings.AccountSettingsRequestDTO;
import com.matchme.srv.dto.request.settings.AttributesSettingsRequestDTO;
import com.matchme.srv.dto.request.settings.PreferencesSettingsRequestDTO;
import com.matchme.srv.dto.request.settings.ProfileSettingsRequestDTO;
import com.matchme.srv.mapper.AttributesMapper;
import com.matchme.srv.mapper.PreferencesMapper;
import com.matchme.srv.model.enums.UserState; // Added enum import
import com.matchme.srv.model.user.User;
import com.matchme.srv.model.user.profile.Hobby;
import com.matchme.srv.model.user.profile.UserProfile;
import com.matchme.srv.model.user.profile.user_attributes.UserAttributes;
import com.matchme.srv.model.user.profile.user_preferences.UserPreferences;
import com.matchme.srv.repository.UserRepository;
import com.matchme.srv.service.HobbyService;
import com.matchme.srv.service.type.UserGenderTypeService;
import com.matchme.srv.service.user.validation.UserValidationService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSettingsService {

    private final UserRepository userRepository;
    private final AttributesMapper attributesMapper;
    private final PreferencesMapper preferencesMapper;
    private final HobbyService hobbyService;
    private final UserGenderTypeService userGenderTypeService;
    private final UserValidationService userValidationService;
   
    private static final String USER_NOT_FOUND_MESSAGE = "User not found!";
   
    public void updateAccountSettings(Long userId, AccountSettingsRequestDTO settings) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MESSAGE));

        log.info("Attempting to update account settings for userId: {}. New email: {}, new number: {}",
                userId, settings.getEmail(), settings.getNumber());

        userValidationService.validateUniqueEmailAndNumber(settings.getEmail(), settings.getNumber(), user.getId());

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
                    .map(hobbyService::getById)
                    .collect(Collectors.toSet());

            profile.setHobbies(foundHobbies);
        } else {
            profile.setHobbies(new HashSet<>());
        }

        log.info("Updating profile settings for user ID: {}", userId);
        checkAndActivateProfile(user);
        userRepository.save(user);
        log.info("Successfully updated profile settings for user ID: {}", userId);
    }

    public void updateAttributesSettings(Long userId, AttributesSettingsRequestDTO settings) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MESSAGE));

        UserProfile profile = user.getProfile();
        UserAttributes attributes = profile.getAttributes();

        attributesMapper.toEntity(attributes, settings);
        attributes.setGender(userGenderTypeService.getById(settings.getGender_self()));
        attributes.setLocation(List.of(settings.getLongitude(), settings.getLatitude()));
        attributes.setBirthdate(settings.getBirth_date());

        profile.setCity(settings.getCity());

        log.info("Updating attributes settings for user ID: {}", userId);
        checkAndActivateProfile(user);
        userRepository.save(user);
        log.info("Successfully updated attributes settings for user ID: {}", userId);
    }

    public void updatePreferencesSettings(Long userId, PreferencesSettingsRequestDTO settings) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MESSAGE));

        UserProfile profile = user.getProfile();
        UserPreferences preferences = profile.getPreferences();

        preferencesMapper.toEntity(preferences, settings);
        preferences.setGender(userGenderTypeService.getById(settings.getGender_other()));
        preferences.setAgeMin(settings.getAge_min());
        preferences.setAgeMax(settings.getAge_max());
        preferences.setDistance(settings.getDistance());
        preferences.setProbabilityTolerance(settings.getProbability_tolerance());

        log.info("Updating preferences settings for user ID: {}", userId);
        userRepository.save(user);
        log.info("Successfully updated preferences settings for user ID: {}", userId);
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
        boolean hobbiesPresent = !CollectionUtils.isEmpty(profile.getHobbies());
        boolean cityPresent = StringUtils.hasText(profile.getCity());
        boolean genderPresent = attributes.getGender() != null;
        boolean birthdatePresent = attributes.getBirthdate() != null;
        boolean locationPresent = attributes.getLocation() != null && attributes.getLocation().size() == 2 &&
        		attributes.getLocation().stream().allMatch(Objects::nonNull);
      
        if (firstNamePresent && lastNamePresent && aliasPresent && hobbiesPresent && cityPresent &&
        		genderPresent && birthdatePresent && locationPresent) {
      
        	log.info("All required profile fields present for user ID: {}. Attempting to activate profile.", user.getId());
        	user.setState(UserState.ACTIVE);
        	log.info("User ID: {} state changed from {} to {}.", user.getId(), UserState.PROFILE_INCOMPLETE, UserState.ACTIVE);
        } else {
        	log.debug("User ID: {} profile still incomplete. State remains {}.", user.getId(), UserState.PROFILE_INCOMPLETE);
        	}
        }
}