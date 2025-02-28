package com.matchme.srv.model.connection;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
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

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DatingPoolSyncService {

    private final MatchingRepository matchingRepository;
    private final UserAttributesRepository userAttributesRepository;
    private final UserPreferencesRepository userPreferencesRepository;
    private final UserScoreRepository userScoreRepository;
    private final UserProfileRepository userProfileRepository;

    @Transactional
    public void synchronizeDatingPool(Long profileId) {

        UserAttributes attributes = userAttributesRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("userAttributes for " + profileId.toString()));

        UserPreferences preferences = userPreferencesRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("userPreferences for " + profileId.toString()));

        UserScore userScore = userScoreRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("userScore for " + profileId.toString()));

        UserProfile userProfile = userProfileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("userProfile for " + profileId.toString()));

        DatingPool entry = matchingRepository.findById(profileId)
                .orElseGet(() -> {
                    DatingPool newEntry = new DatingPool();
                    newEntry.setUserId(profileId);
                    return newEntry;
                });

        // If we don't enforce that all fields in UserAttributes and UserPreferences are
        // not null
        // then additional checks are needed.

        // update gender-related fields
        entry.setMyGender(attributes.getGender().getId());
        entry.setLookingForGender(preferences.getGender().getId());

        // update age-related fields
        entry.setMyAge(getAgeFromBirthDate(attributes.getBirthdate()));
        entry.setAgeMin(preferences.getAgeMin());
        entry.setAgeMax(preferences.getAgeMax());

        // transform location and distance to geohashes and update
        entry.setMyLocation(getGeohashFromCoordinates(attributes.getLocation()));
        entry.setSuitableGeoHashes(
                getSuitableGeohashesWithinDistance(entry.getMyLocation(), preferences.getDistance()));

        // update userScore - possibly should be it's own update due to frequency?
        entry.setActualScore(userScore.getCurrentScore());

        // update the hobbyIds
        entry.setHobbyIds(getHobbyIdsFromHobbies(userProfile.getHobbies()));

        matchingRepository.save(entry);
        log.debug("DatingPool synchronized for profile ID: {}", profileId);
    }

    private Integer getAgeFromBirthDate(LocalDate birthdate) {
        return Period.between(birthdate, LocalDate.now()).getYears();
    }

    private String getGeohashFromCoordinates(List<Double> location) {

        return "bcde234";
    }

    private Set<String> getSuitableGeohashesWithinDistance(String location, Integer distance) {

        return Set.of();
    }

    private Set<Long> getHobbyIdsFromHobbies(Set<Hobby> hobbies) {

        return Set.of();
    }

}
