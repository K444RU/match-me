package com.matchme.srv.model.connection;

import com.matchme.srv.exception.ResourceNotFoundException;
import com.matchme.srv.model.user.profile.Hobby;
import com.matchme.srv.model.user.profile.UserProfile;
import com.matchme.srv.model.user.profile.user_attributes.UserAttributes;
import com.matchme.srv.model.user.profile.user_preferences.UserPreferences;
import com.matchme.srv.model.user.profile.user_score.UserScore;
import com.matchme.srv.repository.*;
import com.matchme.srv.service.GeohashService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Service responsible for synchronizing user profile data with the dating pool system.
 * This service ensures that the {@link DatingPool} entry for a user reflects the latest
 * information from their {@link UserProfile}, {@link UserAttributes}, {@link UserPreferences},
 * and {@link UserScore}. The synchronization is triggered by changes to these entities
 * and is essential for maintaining accurate matching recommendations.
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
     * <p>
     * This method performs the following steps:
     * 1. Retrieves the user's {@link UserAttributes}, {@link UserPreferences}, and {@link UserProfile}.
     * 2. Checks if all required fields are present in these entities. If any required data is missing,
     *    the synchronization is skipped, and a debug log is recorded.
     * 3. Fetches or creates a {@link DatingPool} entry for the user.
     * 4. Updates the {@link DatingPool} entry with the latest user data, including gender, age,
     *    location (as a geohash), hobby IDs, and matching preferences.
     * 5. Saves the updated {@link DatingPool} entry to the repository.
     * <p>
     * The synchronization is skipped if any of the following conditions are met:
     * - {@link UserAttributes} is missing or lacks required fields (gender, birthdate, location).
     * - {@link UserPreferences} is missing or lacks required fields (gender, ageMin, ageMax, distance).
     * - {@link UserProfile} is missing.
     * <p>
     * If the {@link UserScore} is missing when creating a new {@link DatingPool} entry,
     * a {@link ResourceNotFoundException} is thrown.
     *
     * @param profileId The unique identifier of the user profile to synchronize
     * @throws ResourceNotFoundException if the {@link UserScore} cannot be found when creating a new {@link DatingPool} entry
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void synchronizeDatingPool(Long profileId) {
        Optional<UserAttributes> attributesOpt = userAttributesRepository.findById(profileId);
        if (attributesOpt.isEmpty() || !isAttributesComplete(attributesOpt.get())) {
            log.debug("UserAttributes not found for profile ID: {}, skipping sync", profileId);
            return;
        }
        UserAttributes attributes = attributesOpt.get();

        Optional<UserPreferences> preferencesOpt = userPreferencesRepository.findById(profileId);
        if (preferencesOpt.isEmpty() || !isPreferencesComplete(preferencesOpt.get())) {
            log.debug("UserPreferences not found for profile ID: {}, skipping sync", profileId);
            return;
        }
        UserPreferences preferences = preferencesOpt.get();

        Optional<UserProfile> userProfileOpt = userProfileRepository.findById(profileId);
        if (userProfileOpt.isEmpty()) {
            log.debug("UserProfile not found for profile ID: {}, skipping sync", profileId);
            return;
        }
        UserProfile userProfile = userProfileOpt.get();

        DatingPool entry = matchingRepository.findById(profileId)
                .orElseGet(() -> createNewDatingPoolEntry(profileId));

        updateDatingPoolEntry(entry, attributes, preferences, userProfile);
        matchingRepository.save(entry);
        log.debug("DatingPool synchronized for profile ID: {}", profileId);
    }

    /**
     * Creates a new {@link DatingPool} entry for a user.
     * <p>
     * This method is called when no existing {@link DatingPool} entry is found for the user.
     * It retrieves the user's {@link UserScore} and initializes a new {@link DatingPool} entry
     * with the profile ID and the current score.
     *
     * @param profileId The unique identifier of the user profile
     * @return A new {@link DatingPool} entry initialized with the user's profile ID and score
     * @throws ResourceNotFoundException if the {@link UserScore} cannot be found
     */
    private DatingPool createNewDatingPoolEntry(Long profileId) {
        UserScore userScore = userScoreRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("UserScore for profile ID: " + profileId));
        DatingPool newEntry = new DatingPool();
        newEntry.setProfileId(profileId);
        newEntry.setActualScore(userScore.getCurrentScore());
        return newEntry;
    }

    /**
     * Checks if the {@link UserAttributes} entity contains all required fields for synchronization.
     *
     * @param attributes The {@link UserAttributes} entity to check
     * @return {@code true} if gender, birthdate, and location are present; {@code false} otherwise
     */
    private boolean isAttributesComplete(UserAttributes attributes) {
        return attributes.getGender() != null &&
                attributes.getBirthdate() != null &&
                !attributes.getLocation().isEmpty();
    }

    /**
     * Checks if the {@link UserPreferences} entity contains all required fields for synchronization.
     *
     * @param preferences The {@link UserPreferences} entity to check
     * @return {@code true} if gender, ageMin, ageMax, and distance are present; {@code false} otherwise
     */
    private boolean isPreferencesComplete(UserPreferences preferences) {
        return preferences.getGender() != null &&
                preferences.getAgeMin() != null &&
                preferences.getAgeMax() != null &&
                preferences.getDistance() != null;
    }

    /**
     * Updates a dating pool entry with the latest user data.
     *
     * @param entry       The dating pool entry to update
     * @param attributes  The user's attributes
     * @param preferences The user's preferences
     * @param userProfile The user's profile
     */
    private void updateDatingPoolEntry(DatingPool entry, UserAttributes attributes,
                                       UserPreferences preferences, UserProfile userProfile) {
        entry.setMyGender(attributes.getGender());
        entry.setLookingForGender(preferences.getGender());

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
     * Calculates the current age of a user based on their birthdate.
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