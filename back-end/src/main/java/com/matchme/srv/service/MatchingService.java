package com.matchme.srv.service;

import com.matchme.srv.dto.response.ConnectionsDTO;
import com.matchme.srv.dto.response.MatchingRecommendationsDTO;
import com.matchme.srv.exception.PotentialMatchesNotFoundException;
import com.matchme.srv.exception.ResourceNotFoundException;
import com.matchme.srv.model.connection.ConnectionProvider;
import com.matchme.srv.model.connection.DatingPool;
import com.matchme.srv.model.connection.DismissedRecommendation;
import com.matchme.srv.model.user.profile.UserProfile;
import com.matchme.srv.repository.DismissRecommendationRepository;
import com.matchme.srv.repository.MatchingRepository;
import com.matchme.srv.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Service responsible for finding and ranking potential matches for users.
 * Implements a sophisticated matching algorithm that considers
 * multiple factors including:
 * - ELO-based compatibility scoring
 * - Mutual hobbies and interests
 * - Geographic proximity
 * - Age preferences
 * - Gender preferences
 */
@Service
@RequiredArgsConstructor
public class MatchingService {

  /**
   * Matching algorithm constants:
   * - MINIMUM_PROBABILITY: Minimum probability threshold for considering a match
   * (below this are filtered out)
   * - MAXIMUM_PROBABILITY: Maximum probability threshold to prevent overmatching
   * - DEFAULT_MAX_RESULTS: Maximum number of matches to return in a single
   * request
   * - SCALING_FACTOR: Affects how much ELO score differences impact match
   * probability
   * - USER_PROFILE_NOT_FOUND_MESSAGE: Error message for missing user profiles
   */
  private static final double MINIMUM_PROBABILITY = 0.3;
  private static final int DEFAULT_MAX_RESULTS = 10;
  private static final double SCALING_FACTOR = 1071.0;
  private static final double MAXIMUM_PROBABILITY = 0.91;
  private static final String USER_PROFILE_NOT_FOUND_MESSAGE = "UserProfile for user ";

  private final MatchingRepository matchingRepository;
  private final UserProfileRepository userProfileRepository;
  private final ConnectionService connectionService;
  private final DismissRecommendationRepository dismissRecommendationRepository;

  /**
   * Retrieves and constructs detailed recommendation profiles for potential
   * matches.
   * This method combines the matching algorithm results with user profiles to
   * create a set of 10 recommendations.
   * The process involves:
   * 1. Retrieving the requesting user's profile and location
   * 2. Getting potential matches using the matching algorithm
   * 3. Enriching each match with detailed profile information.
   * 
   * @param userId The ID of the user requesting recommendations
   * @return MatchingRecommendationsDTO containing a list of recommended users
   * @throws ResourceNotFoundException         if user profiles are not found
   * @throws PotentialMatchesNotFoundException if no potential matches are found
   */
  public MatchingRecommendationsDTO getRecommendations(Long userId) {
    try {
      UserProfile myProfile = userProfileRepository.findById(userId)
              .orElseThrow(() -> new ResourceNotFoundException(USER_PROFILE_NOT_FOUND_MESSAGE + userId));

      Long profileId = myProfile.getId();
      ConnectionsDTO connections = connectionService.getConnections(profileId);
      Map<Long, Double> possibleMatches = getPossibleMatches(profileId, connections);

      MatchingRecommendationsDTO response = new MatchingRecommendationsDTO();
      response.setRecommendations(new ArrayList<>(possibleMatches.keySet()));
      return response;
    } catch (PotentialMatchesNotFoundException e) {
      throw new PotentialMatchesNotFoundException(
              "No recommendations available for user " + userId + ": " + e.getMessage());
    }
  }

  /**
   * Retrieves potential matches for a user based on their preferences and
   * attributes,
   * filtering out any users previously dismissed by the requesting user.
   *
   * @param profileId User ID (corresponding to UserProfile ID) to find matches
   *                  for.
   * @return Map of user IDs to match probability scores for valid, non-dismissed
   *         matches, sorted by probability descending, limited to
   *         DEFAULT_MAX_RESULTS.
   * @throws ResourceNotFoundException if the user's DatingPool entry is not
   *                                   found.
   */
  public Map<Long, Double> getPossibleMatches(Long profileId, ConnectionsDTO connections) {
    DatingPool entry = matchingRepository.findById(profileId)
        .orElseThrow(() -> new ResourceNotFoundException(USER_PROFILE_NOT_FOUND_MESSAGE + profileId));

    List<DatingPool> possibleMatches = matchingRepository.findUsersThatMatchParameters(
        entry.getLookingForGender(),
        entry.getMyGender(),
        entry.getMyAge(),
        entry.getAgeMin(),
        entry.getAgeMax(),
        entry.getSuitableGeoHashes(),
        entry.getMyLocation(),
        3);

    if (possibleMatches.isEmpty()) {
      return new LinkedHashMap<>();
    }

    List<Long> dismissedUserIds = dismissRecommendationRepository.findDismissedRecommendationIdByProfileId(profileId);
    List<Long> alreadyInteractedUserIds = extractUserIdsFromConnectionsDTO(connections);

    return possibleMatches.stream()
        .filter(pool -> !dismissedUserIds.contains(pool.getProfileId()))
        .filter(pool -> !alreadyInteractedUserIds.contains(pool.getProfileId()))
        .map(pool -> Map.entry(pool.getProfileId(),
            calculateProbability(entry.getActualScore(), entry.getHobbyIds(), pool)))
        .filter(pair -> pair.getValue() > MINIMUM_PROBABILITY)
        .filter(pair -> pair.getValue() < MAXIMUM_PROBABILITY)
        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
        .limit(DEFAULT_MAX_RESULTS)
        .collect(Collectors.toMap(
            Map.Entry::getKey,
            Map.Entry::getValue,
            (e1, e2) -> e1,
            LinkedHashMap::new));
  }

  private List<Long> extractUserIdsFromConnectionsDTO(ConnectionsDTO connections) {
    if (connections == null) {
      return Collections.emptyList();
    }
    return Stream
            .of(connections.getActive(), connections.getPendingIncoming(), connections.getPendingOutgoing())
            .flatMap(List::stream)
            .map(ConnectionProvider::getUserId)
            .toList();
  }

  /**
   * Calculates the match probability between two users based on their ELO scores
   * and mutual interests.
   * The calculation combines:
   * - Base probability using ELO formula: P(A) = 1 / (1 + 10^((RB - RA)/1071))
   * - Hobby similarity bonus using logarithmic scaling
   * 
   * @param userScore User's ELO score
   * @param hobbies   User's hobby IDs
   * @param entry     Dating pool entry of potential match
   * @return Match probability between 0.0 and 1.0
   */
  private Double calculateProbability(Integer userScore, Set<Long> hobbies, DatingPool entry) {
    Double probability = 1.0 / (1.0 + Math.pow(10, (userScore - entry.getActualScore()) / SCALING_FACTOR));

    int mutualHobbies = 0;
    for (Long hobby : hobbies) {
      if (entry.getHobbyIds().contains(hobby)) {
        mutualHobbies++;
      }
    }

    if (mutualHobbies != 0) {
      probability += (0.2 * (Math.log((double) mutualHobbies + 1) / Math.log((double) hobbies.size() + 1)));
    }

    return probability;
  }

  /**
   * Records that a user has dismissed another user's profile as a potential match
   * recommendation.
   * Once dismissed, the dismissed user should not appear in future
   * recommendations for the initiating user.
   *
   * @param userProfileId             The ID of the user profile initiating the
   *                                  dismissal.
   * @param dismissedRecommendationId The ID of the user profile being dismissed.
   * @throws ResourceNotFoundException if either the initiating user's profile or
   *                                   the dismissed user's profile cannot be
   *                                   found by their respective IDs.
   */
  public void dismissedRecommendation(Long userProfileId, Long dismissedRecommendationId) {
    UserProfile userProfile = userProfileRepository.findById(userProfileId)
        .orElseThrow(() -> new ResourceNotFoundException(USER_PROFILE_NOT_FOUND_MESSAGE + userProfileId));
    UserProfile dismissedUserProfile = userProfileRepository.findById(dismissedRecommendationId)
        .orElseThrow(() -> new ResourceNotFoundException(USER_PROFILE_NOT_FOUND_MESSAGE + dismissedRecommendationId));

    DismissedRecommendation dismissedRecommendation = new DismissedRecommendation();
    dismissedRecommendation.setUserProfile(userProfile);
    dismissedRecommendation.setDismissedUserProfile(dismissedUserProfile);
    dismissRecommendationRepository.save(dismissedRecommendation);
  }

  /**
   * Helper method for AccessValidationService that checks
   * if a specific target user is currently among the calculated top potential
   * matches for the current user.
   *
   * @param currentUserId The ID of the user whose current recommendations should
   *                      be checked.
   * @param targetUserId  The ID of the user to check for presence in the
   *                      recommendations.
   */
  public boolean isRecommended(Long currentUserId, Long targetUserId) {
    Map<Long, Double> possibleMatches = getPossibleMatches(currentUserId, null);
    return possibleMatches.containsKey(targetUserId);
  }
}
