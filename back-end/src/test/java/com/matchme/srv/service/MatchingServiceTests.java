package com.matchme.srv.service;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.matchme.srv.dto.response.MatchingRecommendationsDTO;
import com.matchme.srv.exception.PotentialMatchesNotFoundException;
import com.matchme.srv.exception.ResourceNotFoundException;
import com.matchme.srv.model.connection.DatingPool;
import com.matchme.srv.model.user.profile.Hobby;
import com.matchme.srv.model.user.profile.UserGenderType;
import com.matchme.srv.model.user.profile.UserProfile;
import com.matchme.srv.model.user.profile.user_attributes.UserAttributes;
import com.matchme.srv.repository.MatchingRepository;
import com.matchme.srv.repository.UserProfileRepository;

@ExtendWith(MockitoExtension.class)
public class MatchingServiceTests {

  @Mock
  private MatchingRepository matchingRepository;

  @Mock
  private UserProfileRepository userProfileRepository;

  @InjectMocks
  private MatchingService matchingService;

  private static final Long TEST_USER_ID = 1L;
  private static final Long MATCH_USER_ID = 2L;

  private UserProfile testUserProfile;
  private UserProfile matchUserProfile;
  private DatingPool testUserPool;
  private List<DatingPool> potentialMatches;

  @BeforeEach
  void setUp() {
    // Create test user profile
    testUserProfile = createTestUserProfile(TEST_USER_ID);

    // Create match user profile
    matchUserProfile = createTestUserProfile(MATCH_USER_ID);

    // Create test dating pool entry
    testUserPool = createTestDatingPool(TEST_USER_ID);

    // Create potential matches
    potentialMatches = createPotentialMatches();

  }

  /**
   * Test getting recommendations with valid data
   */
  @Test
  void getRecommendations_ShouldReturnValidRecommendations_WhenDataExists() {
    // Arrange
    when(userProfileRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUserProfile));
    when(matchingRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUserPool));
    when(matchingRepository.findUsersThatMatchParameters(any(), any(), any(), any(), any(), any(), any()))
        .thenReturn(potentialMatches);
    when(userProfileRepository.findById(MATCH_USER_ID)).thenReturn(Optional.of(matchUserProfile));

    // Act
    MatchingRecommendationsDTO result = matchingService.getRecommendations(TEST_USER_ID);

    // Assert
    assertNotNull(result);
    assertNotNull(result.getRecommendations());
    assertFalse(result.getRecommendations().isEmpty());
    assertEquals(potentialMatches.size(), result.getRecommendations().size());

    var firstRecommendation = result.getRecommendations().get(0);
    assertEquals(MATCH_USER_ID, firstRecommendation.getUserId());
    assertEquals("Test", firstRecommendation.getFirstName());
    assertEquals("User2", firstRecommendation.getLastName());
    assertNotNull(firstRecommendation.getDistance());
    assertNotNull(firstRecommendation.getProbability());
    assertNotNull(firstRecommendation.getHobbies());
  }

  /**
   * Test when user profile is not found
   */
  @Test
  void getRecommendations_ShouldThrowResourceNotFoundException_WhenUserProfileNotFound() {
    // Arrange
    when(userProfileRepository.findById(TEST_USER_ID)).thenReturn(Optional.empty());

    // Act & Assert
    Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
      matchingService.getRecommendations(TEST_USER_ID);
    });

    assertTrue(exception.getMessage().contains("UserProfile for user"));
  }

  /**
   * Test when no matches found in dating pool
   */
  @Test
  void getRecommendations_ShouldThrowPotentialMatchesNotFoundException_WhenNoMatches() {
    // Arrange
    when(userProfileRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUserProfile));
    when(matchingRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUserPool));
    when(matchingRepository.findUsersThatMatchParameters(any(), any(), any(), any(), any(), any(), any()))
        .thenReturn(Collections.emptyList());

    // Act & Assert
    Exception exception = assertThrows(PotentialMatchesNotFoundException.class, () -> {
      matchingService.getRecommendations(TEST_USER_ID);
    });

    assertTrue(exception.getMessage().contains("No recommendations available"));
  }

  /**
   * Test getting possible matches
   */
  @Test
  void getPossibleMatches_ShouldReturnMatches_WhenDataExists() {
    // Arrange
    when(matchingRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUserPool));
    when(matchingRepository.findUsersThatMatchParameters(any(), any(), any(), any(), any(), any(), any()))
        .thenReturn(potentialMatches);

    // Act
    Map<Long, Double> result = matchingService.getPossibleMatches(TEST_USER_ID);

    // Assert
    assertNotNull(result);
    assertFalse(result.isEmpty());
    assertTrue(result.containsKey(MATCH_USER_ID));
    assertTrue(result.get(MATCH_USER_ID) > 0.3 && result.get(MATCH_USER_ID) < 0.91);
  }

  /**
   * Test when dating pool entry not found
   */
  @Test
  void getPossibleMatches_ShouldThrowResourceNotFoundException_WhenDatingPoolEntryNotFound() {
    // Arrange
    when(matchingRepository.findById(TEST_USER_ID)).thenReturn(Optional.empty());

    // Act & Assert
    Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
      matchingService.getPossibleMatches(TEST_USER_ID);
    });

    assertTrue(exception.getMessage().contains("User " + TEST_USER_ID));
  }

  /**
   * Test when no potential matches found
   */
  @Test
  void getPossibleMatches_ShouldThrowPotentialMatchesNotFoundException_WhenNoMatches() {
    // Arrange
    when(matchingRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUserPool));
    when(matchingRepository.findUsersThatMatchParameters(any(), any(), any(), any(), any(), any(), any()))
        .thenReturn(Collections.emptyList());

    // Act & Assert
    Exception exception = assertThrows(PotentialMatchesNotFoundException.class, () -> {
      matchingService.getPossibleMatches(TEST_USER_ID);
    });

    assertTrue(exception.getMessage().contains("Potential matches with selected parameters"));
  }

  /**
   * Test when matches found but none meet probability criteria
   */
  @Test
  void getPossibleMatches_ShouldThrowPotentialMatchesNotFoundException_WhenNoMatchesMeetProbabilityCriteria() {
    // Arrange
    when(matchingRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUserPool));

    // Create match with very different score to ensure low probability
    DatingPool lowProbabilityMatch = new DatingPool();
    lowProbabilityMatch.setUserId(3L);
    lowProbabilityMatch.setActualScore(3000); // Very different from test user's 1500
    lowProbabilityMatch.setHobbyIds(new HashSet<>()); // No shared hobbies
    lowProbabilityMatch.setMyGender(2L);
    lowProbabilityMatch.setLookingForGender(1L);

    when(matchingRepository.findUsersThatMatchParameters(any(), any(), any(), any(), any(), any(), any()))
        .thenReturn(Collections.singletonList(lowProbabilityMatch));

    // Act & Assert
    Exception exception = assertThrows(PotentialMatchesNotFoundException.class, () -> {
      matchingService.getPossibleMatches(TEST_USER_ID);
    });

    assertTrue(exception.getMessage().contains("Potential matches within acceptable probability range"));
  }

  /**
   * Test match probability calculation with hobby similarity
   */
  @Test
  void getRecommendations_ShouldConsiderHobbySimilarity() {
    // Arrange
    when(userProfileRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUserProfile));
    when(matchingRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUserPool));

    // Create two matches with same score but different hobby overlap
    DatingPool match1 = createTestDatingPool(2L);
    match1.setHobbyIds(new HashSet<>(Arrays.asList(1L, 2L))); // 2 shared hobbies
    match1.setMyGender(2L);
    match1.setLookingForGender(1L);

    DatingPool match2 = createTestDatingPool(3L);
    match2.setHobbyIds(new HashSet<>(Arrays.asList(3L, 4L))); // 0 shared hobbies
    match2.setMyGender(2L);
    match2.setLookingForGender(1L);

    List<DatingPool> matches = Arrays.asList(match1, match2);

    when(matchingRepository.findUsersThatMatchParameters(any(), any(), any(), any(), any(), any(), any()))
        .thenReturn(matches);

    // Mock user profiles
    when(userProfileRepository.findById(2L)).thenReturn(Optional.of(createTestUserProfile(2L)));
    when(userProfileRepository.findById(3L)).thenReturn(Optional.of(createTestUserProfile(3L)));

    // Act
    MatchingRecommendationsDTO result = matchingService.getRecommendations(TEST_USER_ID);

    // Assert
    assertNotNull(result);
    assertEquals(2, result.getRecommendations().size());

    // Match with shared hobbies should have higher probability
    double probMatch1 = result.getRecommendations().stream()
        .filter(r -> r.getUserId().equals(2L))
        .findFirst()
        .get()
        .getProbability();

    double probMatch2 = result.getRecommendations().stream()
        .filter(r -> r.getUserId().equals(3L))
        .findFirst()
        .get()
        .getProbability();

    assertTrue(probMatch1 > probMatch2, "Match with shared hobbies should have higher probability");
  }

  /**
   * Test distance calculation
   */
  @Test
  void getRecommendations_ShouldCalculateDistanceCorrectly() {
    // Arrange
    when(userProfileRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUserProfile));
    when(matchingRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUserPool));
    when(matchingRepository.findUsersThatMatchParameters(any(), any(), any(), any(), any(), any(), any()))
        .thenReturn(potentialMatches);

    // Create match user profile with known location
    UserProfile matchProfile = createTestUserProfile(MATCH_USER_ID);
    UserAttributes matchAttributes = matchProfile.getAttributes();
    // Paris coordinates (known distance from London ~344km)
    matchAttributes.setLocation(Arrays.asList(48.8566, 2.3522));
    matchProfile.setAttributes(matchAttributes);

    when(userProfileRepository.findById(MATCH_USER_ID)).thenReturn(Optional.of(matchProfile));

    // Act
    MatchingRecommendationsDTO result = matchingService.getRecommendations(TEST_USER_ID);

    // Assert
    var firstRecommendation = result.getRecommendations().get(0);
    // Expected distance between London and Paris is ~344km
    // Allow for small rounding differences
    assertTrue(Math.abs(firstRecommendation.getDistance() - 344) < 5,
        "Distance calculation should be approximately correct");
  }

  /**
   * Test age calculation
   */
  @Test
  void getRecommendations_ShouldCalculateAgeCorrectly() {
    // Arrange
    when(userProfileRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUserProfile));
    when(matchingRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUserPool));
    when(matchingRepository.findUsersThatMatchParameters(any(), any(), any(), any(), any(), any(), any()))
        .thenReturn(potentialMatches);

    // Create match user profile with specific birthdate (30 years ago)
    UserProfile matchProfile = createTestUserProfile(MATCH_USER_ID);
    UserAttributes matchAttributes = matchProfile.getAttributes();
    matchAttributes.setBirthdate(LocalDate.now().minusYears(30));
    matchProfile.setAttributes(matchAttributes);

    when(userProfileRepository.findById(MATCH_USER_ID)).thenReturn(Optional.of(matchProfile));

    // Act
    MatchingRecommendationsDTO result = matchingService.getRecommendations(TEST_USER_ID);

    // Assert
    var firstRecommendation = result.getRecommendations().get(0);
    assertEquals(30, firstRecommendation.getAge(), "Age calculation should be correct");
  }

  /**
   * Test hobby conversion
   */
  @Test
  void getRecommendations_ShouldConvertHobbiesCorrectly() {
    // Arrange
    when(userProfileRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUserProfile));
    when(matchingRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUserPool));
    when(matchingRepository.findUsersThatMatchParameters(any(), any(), any(), any(), any(), any(), any()))
        .thenReturn(potentialMatches);

    // Create match user profile with specific hobbies
    UserProfile matchProfile = createTestUserProfile(MATCH_USER_ID);
    Set<Hobby> hobbies = new HashSet<>();
    hobbies.add(new Hobby(1L, "Reading", "Leisure", null));
    hobbies.add(new Hobby(2L, "Swimming", "Exercise", null));
    hobbies.add(new Hobby(3L, "Hiking", "Leisure", null));
    matchProfile.setHobbies(hobbies);

    when(userProfileRepository.findById(MATCH_USER_ID)).thenReturn(Optional.of(matchProfile));

    // Act
    MatchingRecommendationsDTO result = matchingService.getRecommendations(TEST_USER_ID);

    // Assert
    var firstRecommendation = result.getRecommendations().get(0);
    assertEquals(3, firstRecommendation.getHobbies().size(), "Should have correct number of hobbies");
    assertTrue(firstRecommendation.getHobbies().contains("Reading"), "Should contain hobby name");
    assertTrue(firstRecommendation.getHobbies().contains("Swimming"), "Should contain hobby name");
    assertTrue(firstRecommendation.getHobbies().contains("Hiking"), "Should contain hobby name");
  }

  /**
   * Test profile picture encoding
   */
  @Test
  void getRecommendations_ShouldEncodeProfilePictureCorrectly() {
    // Arrange
    when(userProfileRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUserProfile));
    when(matchingRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUserPool));
    when(matchingRepository.findUsersThatMatchParameters(any(), any(), any(), any(), any(), any(), any()))
        .thenReturn(potentialMatches);

    // Create match user profile with profile picture
    UserProfile matchProfile = createTestUserProfile(MATCH_USER_ID);
    byte[] pictureData = "test_image_data".getBytes();
    matchProfile.setProfilePicture(pictureData);

    when(userProfileRepository.findById(MATCH_USER_ID)).thenReturn(Optional.of(matchProfile));

    // Act
    MatchingRecommendationsDTO result = matchingService.getRecommendations(TEST_USER_ID);

    // Assert
    var firstRecommendation = result.getRecommendations().get(0);
    assertNotNull(firstRecommendation.getProfilePicture(), "Profile picture should be encoded");
    assertTrue(firstRecommendation.getProfilePicture().startsWith("data:image/png;base64,"),
        "Should have correct base64 image format");
  }

  /**
   * Test null profile picture handling
   */
  @Test
  void getRecommendations_ShouldHandleNullProfilePicture() {
    // Arrange
    when(userProfileRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUserProfile));
    when(matchingRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUserPool));
    when(matchingRepository.findUsersThatMatchParameters(any(), any(), any(), any(), any(), any(), any()))
        .thenReturn(potentialMatches);

    // Create match user profile with null profile picture
    UserProfile matchProfile = createTestUserProfile(MATCH_USER_ID);
    matchProfile.setProfilePicture(null);

    when(userProfileRepository.findById(MATCH_USER_ID)).thenReturn(Optional.of(matchProfile));

    // Act
    MatchingRecommendationsDTO result = matchingService.getRecommendations(TEST_USER_ID);

    // Assert
    var firstRecommendation = result.getRecommendations().get(0);
    assertNull(firstRecommendation.getProfilePicture(), "Profile picture should be null");
  }

  // Helper methods to create test data
  private UserProfile createTestUserProfile(Long userId) {
    UserProfile profile = new UserProfile();
    profile.setId(userId);
    profile.setFirst_name("Test");
    profile.setLast_name("User" + userId);

    UserAttributes attributes = new UserAttributes();
    attributes.setBirthdate(LocalDate.now().minusYears(25));
    attributes.setLocation(Arrays.asList(51.5074, -0.1278)); // London coordinates
    attributes.setGender(new UserGenderType(1L, "Male"));

    Set<Hobby> hobbies = new HashSet<>();
    hobbies.add(new Hobby(1L, "Reading", "Leisure", null));
    hobbies.add(new Hobby(2L, "Swimming", "Excercise", null));

    profile.setHobbies(hobbies);
    profile.setAttributes(attributes);

    return profile;
  }

  private DatingPool createTestDatingPool(Long userId) {
    DatingPool pool = new DatingPool();
    pool.setUserId(userId);
    pool.setMyGender(1L);
    pool.setLookingForGender(2L);
    pool.setMyAge(25);
    pool.setAgeMin(21);
    pool.setAgeMax(30);
    pool.setActualScore(1500); // Default ELO score
    pool.setMyLocation("u10"); // Example geohash
    pool.setSuitableGeoHashes(new HashSet<>(Arrays.asList("u10", "u11", "u12")));
    pool.setHobbyIds(new HashSet<>(Arrays.asList(1L, 2L)));
    return pool;
  }

  private List<DatingPool> createPotentialMatches() {
    List<DatingPool> matches = new ArrayList<>();
    DatingPool match = createTestDatingPool(MATCH_USER_ID);
    match.setMyGender(2L);
    match.setLookingForGender(1L);
    matches.add(match);
    return matches;
  }
}