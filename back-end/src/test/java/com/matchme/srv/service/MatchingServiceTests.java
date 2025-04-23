package com.matchme.srv.service;

import com.matchme.srv.exception.ResourceNotFoundException;
import com.matchme.srv.model.connection.DatingPool;
import com.matchme.srv.model.connection.DismissedRecommendation;
import com.matchme.srv.model.user.profile.UserGenderEnum;
import com.matchme.srv.model.user.profile.UserProfile;
import com.matchme.srv.repository.DismissRecommendationRepository;
import com.matchme.srv.repository.MatchingRepository;
import com.matchme.srv.repository.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchingServiceTests {

  @Mock
  private MatchingRepository matchingRepository;

  @Mock
  private UserProfileRepository userProfileRepository;

  @Mock
  private DismissRecommendationRepository dismissRecommendationRepository;

  @InjectMocks
  private MatchingService matchingService;

  private static final Long TEST_USER_ID = 1L;
  private static final Long OTHER_USER_ID = 2L;

  private DatingPool testUserPool;
  private UserProfile testUserProfile;
  private UserProfile otherUserProfile;

  @BeforeEach
  void setUp() {
    testUserProfile = UserProfile.builder().id(TEST_USER_ID).first_name("Test").last_name("User").build();
    otherUserProfile = UserProfile.builder().id(OTHER_USER_ID).first_name("Other").last_name("User").build();

    // Create test dating pool entry
    testUserPool = createTestDatingPool(TEST_USER_ID);
  }

  @Test
  void getPossibleMatches_ShouldFilterDismissedUsers() {
    // Arrange
    Long dismissedUserId = 3L;
    Long regularMatchUserId = 4L;

    // Setup the user pool for the requesting user
    when(matchingRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUserPool));

    // Mock the repository to return a list of dismissed IDs including dismissedUserId
    when(dismissRecommendationRepository.findDismissedRecommendationIdByProfileId(TEST_USER_ID))
            .thenReturn(Collections.singletonList(dismissedUserId));

    // Create potential matches including the one to be dismissed
    DatingPool dismissedMatchPool = createTestDatingPool(dismissedUserId);
    dismissedMatchPool.setMyGender(UserGenderEnum.FEMALE);
    dismissedMatchPool.setLookingForGender(UserGenderEnum.MALE);
    dismissedMatchPool.setActualScore(1550); // Ensure valid probability

    DatingPool regularMatchPool = createTestDatingPool(regularMatchUserId);
    regularMatchPool.setMyGender(UserGenderEnum.FEMALE);
    regularMatchPool.setLookingForGender(UserGenderEnum.MALE);
    regularMatchPool.setActualScore(1600); // Ensure valid probability

    List<DatingPool> potentialMatchesFromRepo = Arrays.asList(dismissedMatchPool, regularMatchPool);
    when(matchingRepository.findUsersThatMatchParameters(
            eq(UserGenderEnum.FEMALE), eq(UserGenderEnum.MALE), anyInt(), anyInt(), anyInt(), anySet(), anyString(), anyInt()))
            .thenReturn(potentialMatchesFromRepo);

    // Act
    Map<Long, Double> result = matchingService.getPossibleMatches(TEST_USER_ID);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.size(), "Should only contain one match after filtering");
    assertTrue(result.containsKey(regularMatchUserId), "Should contain the non-dismissed user");
    assertFalse(result.containsKey(dismissedUserId), "Should NOT contain the dismissed user");

    // Verify repository calls
    verify(matchingRepository).findById(TEST_USER_ID);
    verify(dismissRecommendationRepository).findDismissedRecommendationIdByProfileId(TEST_USER_ID);
    verify(matchingRepository).findUsersThatMatchParameters(any(), any(), anyInt(), anyInt(), anyInt(), anySet(), anyString(), anyInt());
  }

  @Test
  void dismissedRecommendation_ShouldSaveDismissal() {
    // Arrange
    when(userProfileRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUserProfile));
    when(userProfileRepository.findById(OTHER_USER_ID)).thenReturn(Optional.of(otherUserProfile));

    // Use ArgumentCaptor to capture the object passed to save
    ArgumentCaptor<DismissedRecommendation> dismissalCaptor = ArgumentCaptor.forClass(DismissedRecommendation.class);

    // Mock the save method (optional, but good practice to ensure it returns the saved object)
    when(dismissRecommendationRepository.save(any(DismissedRecommendation.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    matchingService.dismissedRecommendation(TEST_USER_ID, OTHER_USER_ID);

    // Assert & Verify
    // Verify save was called once
    verify(dismissRecommendationRepository).save(dismissalCaptor.capture());

    // Check the captured dismissal object
    DismissedRecommendation savedDismissal = dismissalCaptor.getValue();
    assertNotNull(savedDismissal);
    assertEquals(testUserProfile, savedDismissal.getUserProfile());
    assertEquals(otherUserProfile, savedDismissal.getDismissedUserProfile());

    // Verify user profiles were fetched
    verify(userProfileRepository).findById(TEST_USER_ID);
    verify(userProfileRepository).findById(OTHER_USER_ID);
  }

  @Test
  void dismissedRecommendation_ShouldThrowResourceNotFound_WhenDismissingUserNotFound() {
    // Arrange
    when(userProfileRepository.findById(TEST_USER_ID)).thenReturn(Optional.empty());
    // No need to mock the other user or save in this case

    // Act & Assert
    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
      matchingService.dismissedRecommendation(TEST_USER_ID, OTHER_USER_ID);
    });

    assertTrue(exception.getMessage().contains(TEST_USER_ID.toString()));
    verify(userProfileRepository).findById(TEST_USER_ID);
    // Ensure findById for the other user and save are NOT called
    verify(userProfileRepository, times(0)).findById(OTHER_USER_ID);
    verify(dismissRecommendationRepository, never()).save(any());
  }


  @Test
  void dismissedRecommendation_ShouldThrowResourceNotFound_WhenDismissedUserNotFound() {
    // Arrange
    when(userProfileRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUserProfile));
    when(userProfileRepository.findById(OTHER_USER_ID)).thenReturn(Optional.empty());

    // Act & Assert
    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
      matchingService.dismissedRecommendation(TEST_USER_ID, OTHER_USER_ID);
    });

    assertTrue(exception.getMessage().contains(OTHER_USER_ID.toString()));
    verify(userProfileRepository).findById(TEST_USER_ID);
    verify(userProfileRepository).findById(OTHER_USER_ID);
    verify(dismissRecommendationRepository, never()).save(any());
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
    UserGenderEnum lookingForGender = UserGenderEnum.FEMALE; // Looking for female
    UserGenderEnum myGender = UserGenderEnum.MALE; // I am male
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
    nonMatchingGenderUser.setMyGender(UserGenderEnum.MALE); // This user is male (not what we're looking for)
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
    matchWithoutMutualHobbies.setMyGender(UserGenderEnum.FEMALE); // Female
    matchWithoutMutualHobbies.setLookingForGender(UserGenderEnum.MALE); // Looking for male
    matchWithoutMutualHobbies.setActualScore(1600); // Different score from test user's 1500
    matchWithoutMutualHobbies.setHobbyIds(new HashSet<>(Arrays.asList(3L, 4L))); // No overlap with test user's hobbies
                                                                                 // (1L, 2L)

    List<DatingPool> potentialMatches = Collections.singletonList(matchWithoutMutualHobbies);

    when(matchingRepository.findUsersThatMatchParameters(
        eq(UserGenderEnum.FEMALE), eq(UserGenderEnum.MALE), anyInt(), anyInt(), anyInt(), anySet(), anyString(), anyInt()))
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
    matchWithMutualHobbies.setMyGender(UserGenderEnum.FEMALE); // Female
    matchWithMutualHobbies.setLookingForGender(UserGenderEnum.MALE); // Looking for male
    matchWithMutualHobbies.setActualScore(1600); // Different score from test user's 1500
    matchWithMutualHobbies.setHobbyIds(new HashSet<>(Arrays.asList(1L, 2L, 3L))); // 2 shared hobbies (1L, 2L)

    List<DatingPool> potentialMatches = Collections.singletonList(matchWithMutualHobbies);

    when(matchingRepository.findUsersThatMatchParameters(
        eq(UserGenderEnum.FEMALE), eq(UserGenderEnum.MALE), anyInt(), anyInt(), anyInt(), anySet(), anyString(), anyInt()))
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
    highProbabilityMatch.setMyGender(UserGenderEnum.FEMALE);
    highProbabilityMatch.setLookingForGender(UserGenderEnum.MALE);
    highProbabilityMatch.setActualScore(3000); // Much higher score than user's 1500 -> high probability
    highProbabilityMatch.setHobbyIds(new HashSet<>(Arrays.asList(1L, 2L))); // All mutual hobbies to push probability
                                                                            // higher

    // 2. Match with very low probability (should be filtered out for being < 0.3)
    DatingPool lowProbabilityMatch = createTestDatingPool(4L);
    lowProbabilityMatch.setMyGender(UserGenderEnum.FEMALE);
    lowProbabilityMatch.setLookingForGender(UserGenderEnum.MALE);
    lowProbabilityMatch.setActualScore(700); // Much lower score than user's 1500 -> low probability
    lowProbabilityMatch.setHobbyIds(new HashSet<>()); // No mutual hobbies to keep probability low

    // 3. Match with acceptable probability (should be included)
    DatingPool acceptableProbabilityMatch = createTestDatingPool(5L);
    acceptableProbabilityMatch.setMyGender(UserGenderEnum.FEMALE);
    acceptableProbabilityMatch.setLookingForGender(UserGenderEnum.MALE);
    acceptableProbabilityMatch.setActualScore(1600); // Score to ensure 0.3 < probability < 0.91
    acceptableProbabilityMatch.setHobbyIds(new HashSet<>(Arrays.asList(1L, 3L))); // One mutual hobby

    List<DatingPool> allMatches = Arrays.asList(
        highProbabilityMatch,
        lowProbabilityMatch,
        acceptableProbabilityMatch);

    when(matchingRepository.findUsersThatMatchParameters(
        eq(UserGenderEnum.FEMALE), eq(UserGenderEnum.MALE), anyInt(), anyInt(), anyInt(), anySet(), anyString(), anyInt()))
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
      match.setMyGender(UserGenderEnum.FEMALE);
      match.setLookingForGender(UserGenderEnum.MALE);
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
        eq(UserGenderEnum.FEMALE), eq(UserGenderEnum.MALE), anyInt(), anyInt(), anyInt(), anySet(), anyString(), anyInt()))
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
    pool.setMyGender(UserGenderEnum.MALE);
    pool.setLookingForGender(UserGenderEnum.FEMALE);
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