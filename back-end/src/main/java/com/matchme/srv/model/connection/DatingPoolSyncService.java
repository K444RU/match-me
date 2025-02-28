package com.matchme.srv.model.connection;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.matchme.srv.exception.ResourceNotFoundException;
import com.matchme.srv.model.user.profile.Hobby;
import com.matchme.srv.model.user.profile.UserProfile;
import com.matchme.srv.model.user.profile.user_attributes.UserAttributes;
import com.matchme.srv.model.user.profile.user_preferences.UserPreferences;
import com.matchme.srv.model.user.profile.user_score.UserScore;
import com.matchme.srv.repository.MatchingRepository;
import com.matchme.srv.repository.UserAttributesRepository;
import com.matchme.srv.repository.UserPreferencesRepository;
import com.matchme.srv.repository.UserProfileRepository;
import com.matchme.srv.repository.UserScoreRepository;
import com.matchme.srv.service.GeohashService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service responsible for synchronizing UserProfile, UserAttributes,
 * UserPreferences and UserScore data with the dating pool system.
 * This service maintains the dating pool entries by updating user attributes,
 * preferences,
 * and matching criteria in a consolidated format optimized for the matching
 * process.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DatingPoolSyncService {

    private final MatchingRepository matchingRepository;
    private final UserAttributesRepository userAttributesRepository;
    private final UserPreferencesRepository userPreferencesRepository;
    private final UserScoreRepository userScoreRepository;
    private final UserProfileRepository userProfileRepository;
    private final GeohashService geohashService;

    /**
     * Synchronizes a user's dating pool entry with their current profile data.
     *
     * @param profileId The unique identifier of the user profile to synchronize
     * @throws ResourceNotFoundException if any required user data entities cannot
     *                                   be found
     */
    @Transactional
    public void synchronizeDatingPool(Long profileId) {

        UserAttributes attributes = userAttributesRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("userAttributes for " + profileId.toString()));

        if (attributes.getGender() == null || attributes.getBirthdate() == null || attributes.getLocation().isEmpty()) {
            log.debug("Missing required fields in userAttributes {}, skipping sync", profileId);
            return;
        }

        UserPreferences preferences = userPreferencesRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("userPreferences for " + profileId.toString()));

        if (preferences.getGender() == null || preferences.getAgeMin() == null || preferences.getAgeMax() == null
                || preferences.getDistance() == null) {
            log.debug("Missing required fields in userPreferences {}, skipping sync", profileId);
            return;
        }

        UserProfile userProfile = userProfileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("userProfile for " + profileId.toString()));

        DatingPool entry = matchingRepository.findById(profileId)
                .orElseGet(() -> {
                    UserScore userScore = userScoreRepository.findById(profileId)
                            .orElseThrow(() -> new ResourceNotFoundException("userScore for " + profileId.toString()));

                    DatingPool newEntry = new DatingPool();
                    newEntry.setUserId(profileId);
                    newEntry.setActualScore(userScore.getCurrentScore());
                    return newEntry;
                });

        // Batch updates
        updateDatingPoolEntry(entry, attributes, preferences, userProfile);
        matchingRepository.save(entry);
        log.debug("DatingPool synchronized for profile ID: {}", profileId);
    }

    /**
     * Synchronizes user preferences with their dating pool entry.
     * This method only updates preference-related fields if a dating pool entry
     * exists.
     *
     * @param profileId The unique identifier of the user profile
     */
    @Transactional
    public void synchronizeUserPreferences(Long profileId) {
        DatingPool entry = matchingRepository.findById(profileId).orElse(null);
        if (entry == null) {
            return;
        }

        UserPreferences preferences = userPreferencesRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("userPreferences for " + profileId.toString()));

        if (preferences.getGender() == null || preferences.getAgeMin() == null || preferences.getAgeMax() == null
                || preferences.getDistance() == null) {
            log.debug("Missing required fields in userPreferences {}, skipping sync", profileId);
            return;
        }

        entry.setLookingForGender(preferences.getGender().getId());
        entry.setAgeMin(preferences.getAgeMin());
        entry.setAgeMax(preferences.getAgeMax());

        entry.setSuitableGeoHashes(
                geohashService.findGeohashesWithinRadius(entry.getMyLocation(), preferences.getDistance()));

        matchingRepository.save(entry);
        log.debug("DatingPool UserPreferences synchronized for profile ID: {}", profileId);
    }

    /**
     * Synchronizes only the user's score in the dating pool.
     * This is optimized for frequent score updates.
     *
     * @param profileId The unique identifier of the user profile
     * @throws ResourceNotFoundException if the user score or dating pool entry
     *                                   cannot be found
     */
    @Transactional
    public void synchronizeUserScore(Long profileId) {
        UserScore userScore = userScoreRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("userScore for " + profileId.toString()));

        DatingPool entry = matchingRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("DatingPool entry for " + profileId));

        entry.setActualScore(userScore.getCurrentScore());
        matchingRepository.save(entry);
        log.debug("DatingPool score synchronized for profile ID: {}", profileId);
    }

    /**
     * Updates a dating pool entry with the latest user data.
     * This helper method consolidates all update operations for better readability
     *
     * @param entry       The dating pool entry to update
     * @param attributes  The user's attributes
     * @param preferences The user's preferences
     * @param userProfile The user's profile
     */
    private void updateDatingPoolEntry(DatingPool entry, UserAttributes attributes,
            UserPreferences preferences, UserProfile userProfile) {

        entry.setMyGender(attributes.getGender().getId());
        entry.setLookingForGender(preferences.getGender().getId());

        entry.setMyAge(getAgeFromBirthDate(attributes.getBirthdate()));
        entry.setAgeMin(preferences.getAgeMin());
        entry.setAgeMax(preferences.getAgeMax());

        String location = geohashService.coordinatesToGeohash(attributes.getLocation());
        entry.setMyLocation(location);
        entry.setSuitableGeoHashes(
                geohashService.findGeohashesWithinRadius(location, preferences.getDistance()));

        entry.setHobbyIds(getHobbyIdsFromHobbies(userProfile.getHobbies()));
    }

    /**
     * Calculates the current age of a user based on their birth date.
     *
     * @param birthdate The user's date of birth
     * @return The calculated age in years
     */
    private Integer getAgeFromBirthDate(LocalDate birthdate) {
        return Period.between(birthdate, LocalDate.now()).getYears();
    }

    /**
     * Extracts hobby IDs from a set of Hobby entities.
     *
     * @param hobbies Set of Hobby entities to process
     * @return Set of hobby IDs as Long values
     */
    private Set<Long> getHobbyIdsFromHobbies(Set<Hobby> hobbies) {
        Set<Long> hobbyIds = new HashSet<>();

        for (Hobby hobby : hobbies) {
            hobbyIds.add(hobby.getId());
        }

        return hobbyIds;
    }
}
