package com.matchme.srv.service;

import java.util.Collections;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.matchme.srv.exception.PotentialMatchesNotFoundException;
import com.matchme.srv.exception.ResourceNotFoundException;
import com.matchme.srv.model.connection.DatingPool;

import com.matchme.srv.repository.MatchingRepository;
import com.matchme.srv.repository.UserProfileRepository;

@ExtendWith(MockitoExtension.class)
class MatchingServiceTests {

  @Mock
  private MatchingRepository matchingRepository;

  @Mock
  private UserProfileRepository userProfileRepository;

  @InjectMocks
  private MatchingService matchingService;

  private static final Long TEST_USER_ID = 1L;

  private DatingPool testUserPool;

  @BeforeEach
  void setUp() {
    // Create test dating pool entry
    testUserPool = createTestDatingPool(TEST_USER_ID);
  }

  @Test
  void getPossibleMatches_ShouldThrowResourceNotFoundException_WhenDatingPoolEntryNotFound() {
    // Arrange
    when(matchingRepository.findById(TEST_USER_ID)).thenReturn(Optional.empty());

    // Act & Assert
    Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
      matchingService.getPossibleMatches(TEST_USER_ID);
    });

    // Verify the exception message contains the user ID
    assertTrue(exception.getMessage().contains(TEST_USER_ID.toString()),
        "Exception message should mention the user ID");

    // Verify that the repository was called with the correct user ID
    verify(matchingRepository).findById(TEST_USER_ID);

  }

  /**
   * Tests that the matching repository correctly filters users based on search
   * parameters
   * and only returns users that match all criteria.
   */
  @Test
  void findUsersThatMatchParameters_ShouldReturnOnlyUsersMatchingAllCriteria() {
    // Arrange
    Long lookingForGender = 2L; // Looking for female
    Long myGender = 1L; // I am male
    Integer myAge = 25;
    Integer ageMin = 21;
    Integer ageMax = 30;
    Set<String> suitableGeoHashes = new HashSet<>(Arrays.asList("u10abc", "u11def", "u12ghi"));
    String myLocation = "u10abc";

    // Create matching and non-matching users
    DatingPool matchingUser = createTestDatingPool(2L);
    matchingUser.setMyGender(lookingForGender); // This user is female (what we're looking for)
    matchingUser.setLookingForGender(myGender); // This user is looking for males (what we are)
    matchingUser.setMyAge(25); // Age within our range
    matchingUser.setMyLocation("u11def"); // Location within our suitable geohashes

    DatingPool nonMatchingGenderUser = createTestDatingPool(3L);
    nonMatchingGenderUser.setMyGender(1L); // This user is male (not what we're looking for)
    nonMatchingGenderUser.setMyLocation("u10abc");

    DatingPool nonMatchingAgeUser = createTestDatingPool(4L);
    nonMatchingAgeUser.setMyGender(lookingForGender);
    nonMatchingAgeUser.setMyAge(35); // Age outside our range
    nonMatchingAgeUser.setMyLocation("u10abc");

    DatingPool nonMatchingLocationUser = createTestDatingPool(5L);
    nonMatchingLocationUser.setMyGender(lookingForGender);
    nonMatchingLocationUser.setMyLocation("z99xyz"); // Location outside our suitable geohashes

    // Mock repository to return only the matching user
    List<DatingPool> matchingUsers = Collections.singletonList(matchingUser);
    when(matchingRepository.findUsersThatMatchParameters(
        eq(lookingForGender), eq(myGender), eq(myAge), eq(ageMin), eq(ageMax),
        eq(suitableGeoHashes), eq(myLocation), eq(3)))
        .thenReturn(matchingUsers);

    // Act
    List<DatingPool> result = matchingRepository.findUsersThatMatchParameters(
        lookingForGender, myGender, myAge, ageMin, ageMax, suitableGeoHashes, myLocation, 3);

    // Assert
    assertNotNull(result, "Result should not be null");
    assertEquals(1, result.size(), "Should return exactly one matching user");
    assertEquals(2L, result.get(0).getProfileId(), "Should return the correct matching user");

    // Verify the non-matching users are not in the result
    assertFalse(result.stream().anyMatch(user -> user.getProfileId().equals(3L)),
        "User with non-matching gender should not be included");
    assertFalse(result.stream().anyMatch(user -> user.getProfileId().equals(4L)),
        "User with non-matching age should not be included");
    assertFalse(result.stream().anyMatch(user -> user.getProfileId().equals(5L)),
        "User with non-matching location should not be included");

    // Verify the repository was called with the correct parameters
    verify(matchingRepository).findUsersThatMatchParameters(
        eq(lookingForGender), eq(myGender), eq(myAge), eq(ageMin), eq(ageMax),
        eq(suitableGeoHashes), eq(myLocation), eq(3));
  }

  /**
   * Tests that matching probabilities are correctly calculated for users without
   * mutual hobbies.
   * The probability calculation should be based on the ELO score difference
   * between users.
   */
  @Test
  void calculateMatchingProbability_ShouldReturnCorrectProbability_WithoutMutualHobbies() {
    // Arrange
    when(matchingRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUserPool));

    // Create a potential match with no mutual hobbies
    DatingPool matchWithoutMutualHobbies = createTestDatingPool(3L);
    matchWithoutMutualHobbies.setMyGender(2L); // Female
    matchWithoutMutualHobbies.setLookingForGender(1L); // Looking for male
    matchWithoutMutualHobbies.setActualScore(1600); // Different score from test user's 1500
    matchWithoutMutualHobbies.setHobbyIds(new HashSet<>(Arrays.asList(3L, 4L))); // No overlap with test user's hobbies
                                                                                 // (1L, 2L)

    List<DatingPool> potentialMatches = Collections.singletonList(matchWithoutMutualHobbies);

    when(matchingRepository.findUsersThatMatchParameters(anyLong(), anyLong(), anyInt(), anyInt(), anyInt(), anySet(), anyString(), anyInt()))
        .thenReturn(potentialMatches);

    // Act
    Map<Long, Double> result = matchingService.getPossibleMatches(TEST_USER_ID);

    // Assert
    assertNotNull(result, "Result should not be null");
    assertEquals(1, result.size(), "Should return exactly one match");
    assertTrue(result.containsKey(3L), "Should contain the match user ID");

    // Calculate expected probability using the ELO formula: 1.0 / (1.0 +
    // Math.pow(10, (userScore - matchScore) / 1071.0))
    // For scores 1500 and 1600, this should be approximately 0.435
    double expectedProbability = 1.0 / (1.0 + Math.pow(10, (1500 - 1600) / 1071.0));
    double actualProbability = result.get(3L);

    // Allow for small rounding differences
    assertEquals(expectedProbability, actualProbability, 0.01,
        "Probability should be calculated correctly based on ELO score difference");

    // Verify the probability is within the acceptable range
    assertTrue(actualProbability > 0.3 && actualProbability < 0.91,
        "Probability should be within the acceptable range (0.3 to 0.91)");

    // Verify repository methods were called
    verify(matchingRepository).findById(TEST_USER_ID);
    verify(matchingRepository).findUsersThatMatchParameters(
        eq(testUserPool.getLookingForGender()),
        eq(testUserPool.getMyGender()),
        eq(testUserPool.getMyAge()),
        eq(testUserPool.getAgeMin()),
        eq(testUserPool.getAgeMax()),
        eq(testUserPool.getSuitableGeoHashes()),
        eq(testUserPool.getMyLocation()),
        eq(3));
  }

  /**
   * Tests that matching probabilities are correctly calculated for users with
   * mutual hobbies.
   * The probability calculation should include a bonus based on the number of
   * shared hobbies.
   */
  @Test
  void calculateMatchingProbability_ShouldReturnIncreasedProbability_WithMutualHobbies() {
    // Arrange
    when(matchingRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUserPool));

    // Create a potential match with mutual hobbies
    DatingPool matchWithMutualHobbies = createTestDatingPool(3L);
    matchWithMutualHobbies.setMyGender(2L); // Female
    matchWithMutualHobbies.setLookingForGender(1L); // Looking for male
    matchWithMutualHobbies.setActualScore(1600); // Different score from test user's 1500
    matchWithMutualHobbies.setHobbyIds(new HashSet<>(Arrays.asList(1L, 2L, 3L))); // 2 shared hobbies (1L, 2L)

    List<DatingPool> potentialMatches = Collections.singletonList(matchWithMutualHobbies);

    when(matchingRepository.findUsersThatMatchParameters(anyLong(), anyLong(), anyInt(), anyInt(), anyInt(), anySet(), anyString(), anyInt()))
        .thenReturn(potentialMatches);

    // Act
    Map<Long, Double> result = matchingService.getPossibleMatches(TEST_USER_ID);

    // Assert
    assertNotNull(result, "Result should not be null");
    assertEquals(1, result.size(), "Should return exactly one match");
    assertTrue(result.containsKey(3L), "Should contain the match user ID");

    // Calculate base probability using the ELO formula: 1.0 / (1.0 + Math.pow(10,
    // (userScore - matchScore) / 1071.0))
    // For scores 1500 and 1600, this should be approximately 0.435
    double baseProbability = 1.0 / (1.0 + Math.pow(10, (1500 - 1600) / 1071.0));

    // Calculate hobby bonus: 0.2 * (Math.log(mutualHobbies + 1) /
    // Math.log(hobbies.size() + 1))
    // For 2 mutual hobbies out of 2 total hobbies: 0.2 * (Math.log(2 + 1) /
    // Math.log(2 + 1)) = 0.2
    double expectedHobbyBonus = 0.2 * (Math.log(2 + 1) / Math.log(2 + 1));

    // Total expected probability
    double expectedProbability = baseProbability + expectedHobbyBonus;
    double actualProbability = result.get(3L);

    // Allow for small rounding differences
    assertEquals(expectedProbability, actualProbability, 0.01,
        "Probability should be increased by the hobby bonus");

    // Verify the probability is higher than the base probability
    assertTrue(actualProbability > baseProbability,
        "Probability with mutual hobbies should be higher than without");

    // Verify the probability is within the acceptable range
    assertTrue(actualProbability > 0.3 && actualProbability < 0.91,
        "Probability should be within the acceptable range (0.3 to 0.91)");

    // Verify repository methods were called
    verify(matchingRepository).findById(TEST_USER_ID);
    verify(matchingRepository).findUsersThatMatchParameters(
        eq(testUserPool.getLookingForGender()),
        eq(testUserPool.getMyGender()),
        eq(testUserPool.getMyAge()),
        eq(testUserPool.getAgeMin()),
        eq(testUserPool.getAgeMax()),
        eq(testUserPool.getSuitableGeoHashes()),
        eq(testUserPool.getMyLocation()),
        eq(3));
  }

  /**
   * Tests that the matching service correctly filters out potential matches with
   * probabilities outside the acceptable range (below 0.3 or above 0.91).
   */
  @Test
  void getPossibleMatches_ShouldFilterOutMatchesOutsideProbabilityThresholds() {
    // Arrange
    when(matchingRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUserPool));

    // Create matches with different probabilities
    // 1. Match with very high probability (should be filtered out for being > 0.91)
    DatingPool highProbabilityMatch = createTestDatingPool(3L);
    highProbabilityMatch.setMyGender(2L);
    highProbabilityMatch.setLookingForGender(1L);
    highProbabilityMatch.setActualScore(3000); // Much higher score than user's 1500 -> high probability
    highProbabilityMatch.setHobbyIds(new HashSet<>(Arrays.asList(1L, 2L))); // All mutual hobbies to push probability
                                                                            // higher

    // 2. Match with very low probability (should be filtered out for being < 0.3)
    DatingPool lowProbabilityMatch = createTestDatingPool(4L);
    lowProbabilityMatch.setMyGender(2L);
    lowProbabilityMatch.setLookingForGender(1L);
    lowProbabilityMatch.setActualScore(700); // Much lower score than user's 1500 -> low probability
    lowProbabilityMatch.setHobbyIds(new HashSet<>()); // No mutual hobbies to keep probability low

    // 3. Match with acceptable probability (should be included)
    DatingPool acceptableProbabilityMatch = createTestDatingPool(5L);
    acceptableProbabilityMatch.setMyGender(2L);
    acceptableProbabilityMatch.setLookingForGender(1L);
    acceptableProbabilityMatch.setActualScore(1600); // Score to ensure 0.3 < probability < 0.91
    acceptableProbabilityMatch.setHobbyIds(new HashSet<>(Arrays.asList(1L, 3L))); // One mutual hobby

    List<DatingPool> allMatches = Arrays.asList(
        highProbabilityMatch,
        lowProbabilityMatch,
        acceptableProbabilityMatch);

    when(matchingRepository.findUsersThatMatchParameters(
        anyLong(), anyLong(), anyInt(), anyInt(), anyInt(), anySet(), anyString(), anyInt()))
        .thenReturn(allMatches);

    // Act
    Map<Long, Double> result = matchingService.getPossibleMatches(TEST_USER_ID);

    // Assert
    assertNotNull(result, "Result should not be null");
    assertEquals(1, result.size(), "Should only include matches with acceptable probability");

    // Verify only the match with acceptable probability is included
    assertTrue(result.containsKey(5L), "Should contain the match with acceptable probability");

    // Verify matches with too low or too high probability are filtered out
    assertFalse(result.containsKey(3L), "Should not contain match with too high probability");
    assertFalse(result.containsKey(4L), "Should not contain match with too low probability");

    // Verify the probability of the included match is within acceptable range
    double probability = result.get(5L);
    assertTrue(probability > 0.3 && probability < 0.91,
        "Probability should be within acceptable range (0.3 to 0.91)");

    // Verify repository methods were called
    verify(matchingRepository).findById(TEST_USER_ID);
    verify(matchingRepository).findUsersThatMatchParameters(
        eq(testUserPool.getLookingForGender()),
        eq(testUserPool.getMyGender()),
        eq(testUserPool.getMyAge()),
        eq(testUserPool.getAgeMin()),
        eq(testUserPool.getAgeMax()),
        eq(testUserPool.getSuitableGeoHashes()),
        eq(testUserPool.getMyLocation()),
        eq(3));
  }

  /**
   * Tests that the matching service correctly sorts potential matches by
   * probability
   * in descending order and limits the results to a maximum of 10 matches.
   */
  @Test
  void getPossibleMatches_ShouldLimitToTop10MatchesInDescendingProbabilityOrder() {
    // Arrange
    when(matchingRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUserPool));

    // Create more than 10 potential matches with different probabilities
    List<DatingPool> manyMatches = new ArrayList<>();
    for (int i = 2; i <= 15; i++) {
      DatingPool match = createTestDatingPool((long) i);
      match.setMyGender(2L);
      match.setLookingForGender(1L);
      // Set different scores to create different probabilities
      match.setActualScore(1500 + (i * 10)); // Increasing scores for higher probabilities

      // Add varying numbers of mutual hobbies
      Set<Long> hobbies = new HashSet<>();
      hobbies.add(1L); // At least one mutual hobby
      if (i % 3 == 0)
        hobbies.add(2L); // Some matches have two mutual hobbies
      match.setHobbyIds(hobbies);

      manyMatches.add(match);
    }

    when(matchingRepository.findUsersThatMatchParameters(
        anyLong(), anyLong(), anyInt(), anyInt(), anyInt(), anySet(), anyString(), anyInt()))
        .thenReturn(manyMatches);

    // Act
    Map<Long, Double> result = matchingService.getPossibleMatches(TEST_USER_ID);

    // Assert
    assertNotNull(result, "Result should not be null");
    assertTrue(result.size() <= 10, "Should return at most 10 matches");

    // Verify the matches are sorted by probability in descending order
    List<Double> probabilities = new ArrayList<>(result.values());
    List<Double> sortedProbabilities = new ArrayList<>(probabilities);
    Collections.sort(sortedProbabilities, Collections.reverseOrder());

    assertEquals(sortedProbabilities, probabilities,
        "Matches should be sorted by probability in descending order");

    // Verify that the matches with highest probabilities are included
    double lowestIncludedProbability = Collections.min(result.values());
    for (DatingPool match : manyMatches) {
      double expectedProbability = calculateExpectedProbability(match);
      if (!result.containsKey(match.getProfileId())) {
        // If a match is not included, its probability should be lower than the lowest
        // included probability
        assertTrue(expectedProbability <= lowestIncludedProbability,
            "Only matches with highest probabilities should be included");
      }
    }

    // Verify repository methods were called
    verify(matchingRepository).findById(TEST_USER_ID);
    verify(matchingRepository).findUsersThatMatchParameters(
        eq(testUserPool.getLookingForGender()),
        eq(testUserPool.getMyGender()),
        eq(testUserPool.getMyAge()),
        eq(testUserPool.getAgeMin()),
        eq(testUserPool.getAgeMax()),
        eq(testUserPool.getSuitableGeoHashes()),
        eq(testUserPool.getMyLocation()),
        eq(3));
  }

  // Helper method to calculate expected probability for a match
  private double calculateExpectedProbability(DatingPool match) {
    // Base probability using ELO formula
    double baseProbability = 1.0 / (1.0 + Math.pow(10, (1500 - match.getActualScore()) / 1071.0));

    // Calculate mutual hobbies
    Set<Long> userHobbies = testUserPool.getHobbyIds();
    Set<Long> matchHobbies = match.getHobbyIds();
    Set<Long> mutualHobbies = new HashSet<>(userHobbies);
    mutualHobbies.retainAll(matchHobbies);

    // Calculate hobby bonus
    double hobbyBonus = 0.0;
    if (!mutualHobbies.isEmpty()) {
      hobbyBonus = 0.2 * (Math.log(mutualHobbies.size() + 1) / Math.log(userHobbies.size() + 1));
    }

    return baseProbability + hobbyBonus;
  }

  private DatingPool createTestDatingPool(Long userId) {
    DatingPool pool = new DatingPool();
    pool.setProfileId(userId);
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
}