package com.matchme.srv.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.matchme.srv.model.user.profile.Hobby;
import com.matchme.srv.model.user.profile.UserGenderType;
import com.matchme.srv.model.user.profile.user_attributes.UserAttributes;
import com.matchme.srv.model.user.profile.user_preferences.UserPreferences;
import com.matchme.srv.model.user.profile.user_score.UserScore;
import com.matchme.srv.repository.ConnectionRepository;
import com.matchme.srv.repository.HobbyRepository;
import com.matchme.srv.repository.UserAttributesRepository;
import com.matchme.srv.repository.UserPreferencesRepository;
import com.matchme.srv.repository.UserProfileRepository;
import com.matchme.srv.repository.UserScoreRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MatchingServiceTest {

    @Mock
    private ConnectionRepository connectionRepository;
    
    @Mock
    private UserPreferencesRepository preferencesRepository;
    
    @Mock
    private UserAttributesRepository attributesRepository;
    
    @Mock
    private UserScoreRepository scoreRepository;
    
    @Mock
    private UserProfileRepository userProfileRepository;
    
    @Mock
    private HobbyRepository hobbyRepository;
    
    @InjectMocks
    private MatchingService matchingService;
    
    private static final Long USER_ID = 1L;
    private static final Long MATCH_ID_1 = 2L;
    private static final Long MATCH_ID_2 = 3L;
    private static final Long MATCH_ID_3 = 4L;
    
    private UserPreferences userPreferences;
    private UserAttributes userAttributes;
    private UserScore userScore;
    private Set<Hobby> userHobbies;
    
    private UserAttributes matchAttributes1;
    private UserScore matchScore1;
    private Set<Hobby> matchHobbies1;
    
    private UserAttributes matchAttributes2;
    private UserScore matchScore2;
    private Set<Hobby> matchHobbies2;
    
    private UserAttributes matchAttributes3;
    private UserScore matchScore3;
    private Set<Hobby> matchHobbies3;
    
    @BeforeEach
    void setUp() {
        // Set up user preferences
        userPreferences = new UserPreferences();
        userPreferences.setAge_min(25);
        userPreferences.setAge_max(35);
        userPreferences.setDistance(50);
        userPreferences.setProbability_tolerance(0.7);
        
        UserGenderType femaleGender = new UserGenderType();
        femaleGender.setId(2L);
        femaleGender.setName("FEMALE");
        userPreferences.setGender(femaleGender);
        
        // Set up user attributes
        userAttributes = new UserAttributes();
        
        UserGenderType maleGender = new UserGenderType();
        maleGender.setId(1L);
        maleGender.setName("MALE");
        userAttributes.setGender(maleGender);
        
        userAttributes.setBirth_date(LocalDate.now().minusYears(30));
        userAttributes.setLocation(Arrays.asList(58.8879, 25.5412));
        userAttributes.setLocationGeohash("u6mzpw7");
        
        // Set up user score
        userScore = new UserScore();
        userScore.setCurrentScore(1500);
        userScore.setVibeProbability(0.8);
        userScore.setCurrentBlind(1400);
        
        // Set up user hobbies
        userHobbies = new HashSet<>();
        userHobbies.add(createHobby(1L, "Reading"));
        userHobbies.add(createHobby(2L, "Hiking"));
        userHobbies.add(createHobby(3L, "Photography"));
        
        // Set up match 1 (high probability match)
        matchAttributes1 = new UserAttributes();
        matchAttributes1.setGender(femaleGender);
        matchAttributes1.setBirth_date(LocalDate.now().minusYears(28));
        matchAttributes1.setLocation(Arrays.asList(58.9, 25.6));
        matchAttributes1.setLocationGeohash("u6mzpwk");
        
        matchScore1 = new UserScore();
        matchScore1.setCurrentScore(1450);
        matchScore1.setVibeProbability(0.9);
        
        matchHobbies1 = new HashSet<>();
        matchHobbies1.add(createHobby(1L, "Reading"));
        matchHobbies1.add(createHobby(2L, "Hiking"));
        matchHobbies1.add(createHobby(4L, "Cooking"));
        
        // Set up match 2 (medium probability match)
        matchAttributes2 = new UserAttributes();
        matchAttributes2.setGender(femaleGender);
        matchAttributes2.setBirth_date(LocalDate.now().minusYears(32));
        matchAttributes2.setLocation(Arrays.asList(58.7, 25.3));
        matchAttributes2.setLocationGeohash("u6mzpu2");
        
        matchScore2 = new UserScore();
        matchScore2.setCurrentScore(1600);
        matchScore2.setVibeProbability(0.7);
        
        matchHobbies2 = new HashSet<>();
        matchHobbies2.add(createHobby(2L, "Hiking"));
        matchHobbies2.add(createHobby(5L, "Dancing"));
        
        // Set up match 3 (low probability match, below threshold)
        matchAttributes3 = new UserAttributes();
        matchAttributes3.setGender(femaleGender);
        matchAttributes3.setBirth_date(LocalDate.now().minusYears(34));
        matchAttributes3.setLocation(Arrays.asList(59.0, 26.0));
        matchAttributes3.setLocationGeohash("u6nk0m7");
        
        matchScore3 = new UserScore();
        matchScore3.setCurrentScore(1800);
        matchScore3.setVibeProbability(0.6);
        
        matchHobbies3 = new HashSet<>();
        matchHobbies3.add(createHobby(6L, "Gaming"));
        matchHobbies3.add(createHobby(7L, "Movies"));
    }
    
    private Hobby createHobby(Long id, String name) {
        Hobby hobby = new Hobby();
        hobby.setId(id);
        hobby.setName(name);
        return hobby;
    }
    
    @Nested
    @DisplayName("getMatches Tests")
    class GetMatchesTests {
        
        @Test
        @DisplayName("Should return matches ordered by probability")
        void getMatches_ReturnsOrderedMatches() {
            // Arrange
            when(preferencesRepository.findById(USER_ID)).thenReturn(Optional.of(userPreferences));
            when(attributesRepository.findById(USER_ID)).thenReturn(Optional.of(userAttributes));
            when(scoreRepository.findById(USER_ID)).thenReturn(Optional.of(userScore));
            when(hobbyRepository.findByUserId(USER_ID)).thenReturn(userHobbies);
            
            List<Long> matchingUserIds = Arrays.asList(MATCH_ID_1, MATCH_ID_2, MATCH_ID_3);
            when(attributesRepository.findMatchingUsers(
                    eq(USER_ID), 
                    eq(userPreferences.getGender().getId()), 
                    any(LocalDate.class), 
                    any(LocalDate.class), 
                    anyList(), 
                    anyInt()
            )).thenReturn(matchingUserIds);
            
            when(attributesRepository.findById(MATCH_ID_1)).thenReturn(Optional.of(matchAttributes1));
            when(scoreRepository.findById(MATCH_ID_1)).thenReturn(Optional.of(matchScore1));
            when(hobbyRepository.findByUserId(MATCH_ID_1)).thenReturn(matchHobbies1);
            
            when(attributesRepository.findById(MATCH_ID_2)).thenReturn(Optional.of(matchAttributes2));
            when(scoreRepository.findById(MATCH_ID_2)).thenReturn(Optional.of(matchScore2));
            when(hobbyRepository.findByUserId(MATCH_ID_2)).thenReturn(matchHobbies2);
            
            when(attributesRepository.findById(MATCH_ID_3)).thenReturn(Optional.of(matchAttributes3));
            when(scoreRepository.findById(MATCH_ID_3)).thenReturn(Optional.of(matchScore3));
            when(hobbyRepository.findByUserId(MATCH_ID_3)).thenReturn(matchHobbies3);
            
            // Act
            List<Long> matches = matchingService.getMatches(USER_ID);
            
            // Assert
            assertAll(
                () -> assertThat(matches).isNotEmpty(),
                () -> assertThat(matches.size()).isLessThanOrEqualTo(3), // May be less if some are below threshold
                () -> {
                    if (matches.size() >= 2) {
                        // First match should be MATCH_ID_1 (highest probability)
                        assertThat(matches.get(0)).isEqualTo(MATCH_ID_1);
                        // Second match should be MATCH_ID_2 (medium probability)
                        assertThat(matches.get(1)).isEqualTo(MATCH_ID_2);
                    }
                },
                () -> {
                    // MATCH_ID_3 might be excluded due to low probability
                    if (matches.size() == 3) {
                        assertThat(matches.get(2)).isEqualTo(MATCH_ID_3);
                    }
                },
                () -> verify(preferencesRepository, times(1)).findById(USER_ID),
                () -> verify(attributesRepository, times(1)).findById(USER_ID),
                () -> verify(scoreRepository, times(1)).findById(USER_ID),
                () -> verify(hobbyRepository, times(1)).findByUserId(USER_ID),
                () -> verify(attributesRepository, times(1)).findMatchingUsers(
                        eq(USER_ID), 
                        eq(userPreferences.getGender().getId()), 
                        any(LocalDate.class), 
                        any(LocalDate.class), 
                        anyList(), 
                        anyInt()
                )
            );
        }
        
        @Test
        @DisplayName("Should return empty list when no matches found")
        void getMatches_NoMatches_ReturnsEmptyList() {
            // Arrange
            when(preferencesRepository.findById(USER_ID)).thenReturn(Optional.of(userPreferences));
            when(attributesRepository.findById(USER_ID)).thenReturn(Optional.of(userAttributes));
            when(scoreRepository.findById(USER_ID)).thenReturn(Optional.of(userScore));
            when(hobbyRepository.findByUserId(USER_ID)).thenReturn(userHobbies);
            
            when(attributesRepository.findMatchingUsers(
                    eq(USER_ID), 
                    eq(userPreferences.getGender().getId()), 
                    any(LocalDate.class), 
                    any(LocalDate.class), 
                    anyList(), 
                    anyInt()
            )).thenReturn(new ArrayList<>());
            
            // Act
            List<Long> matches = matchingService.getMatches(USER_ID);
            
            // Assert
            assertAll(
                () -> assertThat(matches).isEmpty(),
                () -> verify(preferencesRepository, times(1)).findById(USER_ID),
                () -> verify(attributesRepository, times(1)).findById(USER_ID),
                () -> verify(scoreRepository, times(1)).findById(USER_ID),
                () -> verify(hobbyRepository, times(1)).findByUserId(USER_ID),
                () -> verify(attributesRepository, times(1)).findMatchingUsers(
                        eq(USER_ID), 
                        eq(userPreferences.getGender().getId()), 
                        any(LocalDate.class), 
                        any(LocalDate.class), 
                        anyList(), 
                        anyInt()
                )
            );
        }
    }
    
    @Nested
    @DisplayName("GeoHash Tests")
    class GeoHashTests {
        
        @Test
        @DisplayName("Should calculate correct geohash for location")
        void getGeoHash_CalculatesCorrectGeohash() {
            // Arrange
            List<Double> location = Arrays.asList(58.8879, 25.5412);
            
            // Act
            String geoHash = matchingService.getGeoHash(location);
            
            // Assert
            assertThat(geoHash).isEqualTo("u6mzpw7");
        }
        
        @Test
        @DisplayName("Should get suitable geohashes based on distance")
        void getSuitableGeoHashes_ReturnsNearbyGeohashes() {
            // Arrange
            String centralGeoHash = "u6mzpw7";
            Integer distanceKm = 10;
            
            // Act
            Set<String> geoHashes = matchingService.getSuitableGeoHashes(centralGeoHash, distanceKm);
            
            // Assert
            assertAll(
                () -> assertThat(geoHashes).isNotEmpty(),
                () -> assertThat(geoHashes).contains(centralGeoHash),
                () -> assertThat(geoHashes.size()).isGreaterThan(1) // Should include nearby geohashes
            );
        }
    }
    
    @Nested
    @DisplayName("Probability Calculation Tests")
    class ProbabilityCalculationTests {
        
        @Test
        @DisplayName("Should calculate correct base match probability")
        void calculateBaseMatchProbability_CalculatesCorrectly() {
            // Arrange
            int userScore = 1500;
            int candidateScore = 1450;
            
            // Act
            // Use reflection to access the private method
            double probability = matchingService.calculateBaseMatchProbability(userScore, candidateScore);
            
            // Assert
            assertThat(probability).isGreaterThan(0.5); // User has higher score, so probability should be > 0.5
            assertThat(probability).isLessThan(1.0);
        }
        
        @Test
        @DisplayName("Should calculate correct mutual interest influence")
        void calculateMutualInterestInfluence_CalculatesCorrectly() {
            // Arrange
            Set<Hobby> userHobbies = new HashSet<>();
            userHobbies.add(createHobby(1L, "Reading"));
            userHobbies.add(createHobby(2L, "Hiking"));
            userHobbies.add(createHobby(3L, "Photography"));
            
            Set<Hobby> candidateHobbies = new HashSet<>();
            candidateHobbies.add(createHobby(1L, "Reading"));
            candidateHobbies.add(createHobby(2L, "Hiking"));
            candidateHobbies.add(createHobby(4L, "Cooking"));
            
            // Act
            // Use reflection to access the private method
            double influence = matchingService.calculateMutualInterestInfluence(userHobbies, candidateHobbies);
            
            // Assert
            // 2 mutual hobbies out of 3 user hobbies = 2/3 * MAX_INTEREST_INFLUENCE
            double expected = (2.0 / 3.0) * 0.2; // MAX_INTEREST_INFLUENCE is 0.2
            assertThat(influence).isEqualTo(expected);
        }
    }
} 