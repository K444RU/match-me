package com.matchme.srv.model.connection;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.matchme.srv.exception.ResourceNotFoundException;
import com.matchme.srv.model.user.profile.Hobby;
import com.matchme.srv.model.user.profile.UserProfile;
import com.matchme.srv.model.user.profile.user_attributes.UserAttributes;
import com.matchme.srv.model.user.profile.user_preferences.UserPreferences;
import com.matchme.srv.model.user.profile.user_score.UserScore;
import com.matchme.srv.repository.MatchingRepository;
import com.matchme.srv.repository.UserAttributesRepository;
import com.matchme.srv.repository.UserPreferencesRepository;
import com.matchme.srv.repository.UserScoreRepository;
import com.matchme.srv.repository.UserProfileRepository;
import com.matchme.srv.service.GeohashService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service responsible for synchronizing UserProfile, UserAttributes,
 * UserPreferences, and UserScore data with the dating pool system.
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
     * @throws ResourceNotFoundException if any required user data entities cannot be found
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void synchronizeDatingPool(Long profileId) {
        Optional<UserAttributes> attributesOpt = userAttributesRepository.findById(profileId);
        if (attributesOpt.isEmpty()) {
            log.debug("UserAttributes not found for profile ID: {}, skipping sync", profileId);
            return;
        }
        UserAttributes attributes = attributesOpt.get();

        if (attributes.getGender() == null || attributes.getBirthdate() == null || attributes.getLocation().isEmpty()) {
            log.debug("Missing required fields in userAttributes {}, skipping sync", profileId);
            return;
        }

        Optional<UserPreferences> preferencesOpt = userPreferencesRepository.findById(profileId);
        if (preferencesOpt.isEmpty()) {
            log.debug("UserPreferences not found for profile ID: {}, skipping sync", profileId);
            return;
        }
        UserPreferences preferences = preferencesOpt.get();

        if (preferences.getGender() == null || preferences.getAgeMin() == null ||
                preferences.getAgeMax() == null || preferences.getDistance() == null) {
            log.debug("Missing required fields in userPreferences {}, skipping sync", profileId);
            return;
        }

        Optional<UserProfile> userProfileOpt = userProfileRepository.findById(profileId);
        if (userProfileOpt.isEmpty()) {
            log.debug("UserProfile not found for profile ID: {}, skipping sync", profileId);
            return;
        }
        UserProfile userProfile = userProfileOpt.get();

        DatingPool entry = matchingRepository.findById(profileId)
                .orElseGet(() -> {
                    UserScore userScore = userScoreRepository.findById(profileId)
                            .orElseThrow(() -> new ResourceNotFoundException("userScore for " + profileId));
                    DatingPool newEntry = new DatingPool();
                    newEntry.setProfileId(profileId);
                    newEntry.setActualScore(userScore.getCurrentScore());
                    return newEntry;
                });

        updateDatingPoolEntry(entry, attributes, preferences, userProfile);
        matchingRepository.save(entry);
        log.debug("DatingPool synchronized for profile ID: {}", profileId);
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