package com.matchme.srv.service.user;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.matchme.srv.dto.request.settings.AccountSettingsRequestDTO;
import com.matchme.srv.dto.request.settings.AttributesSettingsRequestDTO;
import com.matchme.srv.dto.request.settings.PreferencesSettingsRequestDTO;
import com.matchme.srv.dto.request.settings.ProfileSettingsRequestDTO;
import com.matchme.srv.mapper.AttributesMapper;
import com.matchme.srv.mapper.PreferencesMapper;
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

        // TODO: Add logging
        userRepository.save(user);
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

        // TODO: Add logging
        userRepository.save(user);
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
        preferences.setProbability_tolerance(settings.getProbability_tolerance());

        // TODO: Add logging
        userRepository.save(user);
    }

}
