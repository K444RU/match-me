package com.matchme.srv.service;

import com.matchme.srv.dto.response.ConnectionsDTO;
import com.matchme.srv.dto.response.MatchingRecommendationsDTO;
import com.matchme.srv.dto.response.MatchingRecommendationsDTO.RecommendedUserDTO;
import com.matchme.srv.exception.PotentialMatchesNotFoundException;
import com.matchme.srv.exception.ResourceNotFoundException;
import com.matchme.srv.model.connection.ConnectionProvider;
import com.matchme.srv.model.connection.DatingPool;
import com.matchme.srv.model.connection.DismissedRecommendation;
import com.matchme.srv.model.user.profile.Hobby;
import com.matchme.srv.model.user.profile.UserProfile;
import com.matchme.srv.model.user.profile.user_attributes.UserAttributes;
import com.matchme.srv.repository.DismissRecommendationRepository;
import com.matchme.srv.repository.MatchingRepository;
import com.matchme.srv.repository.UserProfileRepository;
import com.matchme.srv.util.LocationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

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
   * - MINIMUM_PROBABILITY: Minimum probability threshold for considering a match (below this are filtered out)
   * - MAXIMUM_PROBABILITY: Maximum probability threshold to prevent overmatching
   * - DEFAULT_MAX_RESULTS: Maximum number of matches to return in a single request
   * - SCALING_FACTOR: Affects how much ELO score differences impact match probability
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
   * Retrieves and constructs detailed recommendation profiles for potential matches.
   * This method combines the matching algorithm results with user profiles to create a set of 10 recommendations.
   * The process involves:
   * 1. Retrieving the requesting user's profile and location
   * 2. Getting potential matches using the matching algorithm
   * 3. Enriching each match with detailed profile information.
   * @param userId The ID of the user requesting recommendations
   * @return MatchingRecommendationsDTO containing a list of recommended users
   * @throws ResourceNotFoundException if user profiles are not found
   * @throws PotentialMatchesNotFoundException if no potential matches are found
   */
  public MatchingRecommendationsDTO getRecommendations(Long userId) {
    try {
      UserProfile myProfile = userProfileRepository.findById(userId)
              .orElseThrow(() -> new ResourceNotFoundException(USER_PROFILE_NOT_FOUND_MESSAGE + userId));

      Long profileId = myProfile.getId();
      List<Double> myLocation = myProfile.getAttributes().getLocation();
      Map<Long, Double> possibleMatches = getPossibleMatches(profileId);
      ConnectionsDTO connections = connectionService.getConnections(profileId);

      Map<Long, String> connectionStatus = new HashMap<>();
      Map<Long, Long> connectionIds = new HashMap<>();
      populateConnectionMaps(connections, connectionStatus, connectionIds);

      MatchingRecommendationsDTO response = new MatchingRecommendationsDTO();
      List<RecommendedUserDTO> recommendations = buildRecommendations(possibleMatches, myLocation, connectionStatus, connectionIds);
      response.setRecommendations(recommendations);
      return response;
    } catch (PotentialMatchesNotFoundException e) {
      throw new PotentialMatchesNotFoundException(
          "No recommendations available for user " + userId + ": " + e.getMessage());
    }
  }

  /**
   * Helper method for getRecommendations that populates connection status and ID maps from ConnectionsDTO.
   * @param connections DTO containing connection information
   * @param connectionStatus Map to store user ID to connection status
   * @param connectionIds Map to store user ID to connection ID
   */
  private void populateConnectionMaps(ConnectionsDTO connections, Map<Long, String> connectionStatus, Map<Long, Long> connectionIds) {
    for (ConnectionProvider cp : connections.getActive()) {
      connectionStatus.put(cp.getUserId(), "ACCEPTED");
      connectionIds.put(cp.getUserId(), cp.getConnectionId());
    }
    for (ConnectionProvider cp : connections.getPendingOutgoing()) {
      connectionStatus.put(cp.getUserId(), "PENDING_SENT");
      connectionIds.put(cp.getUserId(), cp.getConnectionId());
    }
    for (ConnectionProvider cp : connections.getPendingIncoming()) {
      connectionStatus.put(cp.getUserId(), "PENDING_RECEIVED");
      connectionIds.put(cp.getUserId(), cp.getConnectionId());
    }
  }

  /**
   * Helper method for getRecommendations that builds list of RecommendedUserDTOs from match data.
   * @param possibleMatches Map of user IDs to match probabilities
   * @param myLocation User's location for distance calculation
   * @param connectionStatus Map of user IDs to connection status
   * @param connectionIds Map of user IDs to connection IDs
   * @return List of populated RecommendedUserDTOs
   */
  private List<RecommendedUserDTO> buildRecommendations(Map<Long, Double> possibleMatches, List<Double> myLocation, Map<Long, String> connectionStatus, Map<Long, Long> connectionIds) {
    List<RecommendedUserDTO> recommendations = new ArrayList<>();

    List<UserProfile> profiles = userProfileRepository.findAllById(possibleMatches.keySet());
    Map<Long, UserProfile> profileMap = profiles.stream()
            .collect(Collectors.toMap(UserProfile::getId, p -> p));

    for (Map.Entry<Long, Double> match : possibleMatches.entrySet()) {
      Long matchUserId = match.getKey();
      Double matchScore = match.getValue();

      UserProfile profile = profileMap.get(matchUserId);
      if (profile == null) {
        throw new ResourceNotFoundException(USER_PROFILE_NOT_FOUND_MESSAGE + matchUserId);
      }

      RecommendedUserDTO dto = createRecommendedUserDTO(profile, matchScore, myLocation);
      setConnectionInfo(dto, matchUserId, connectionStatus, connectionIds);
      recommendations.add(dto);
    }

    return recommendations;
  }

  /**
   * Helper method for getRecommendations that sets connection status and ID on the RecommendedUserDTO.
   * @param dto RecommendedUserDTO to update
   * @param matchUserId ID of the matched user
   * @param connectionStatus Map of user IDs to connection status
   * @param connectionIds Map of user IDs to connection IDs
   */
  private void setConnectionInfo(RecommendedUserDTO dto, Long matchUserId, Map<Long, String> connectionStatus, Map<Long, Long> connectionIds) {
    String status = connectionStatus.getOrDefault(matchUserId, "NONE");
    dto.setConnectionStatus(status);
    if (!"NONE".equals(status)) {
      dto.setConnectionId(connectionIds.get(matchUserId));
    }
  }

  /**
   * Helper method for getRecommendations that creates a RecommendedUserDTO from profile data and match score.
   * @param profile UserProfile of the matched user
   * @param matchScore Probability score of the match
   * @param myLocation User's location for distance calculation
   * @return Populated RecommendedUserDTO
   */
  private RecommendedUserDTO createRecommendedUserDTO(UserProfile profile, Double matchScore, List<Double> myLocation) {
    String base64Picture = null;
    if (profile.getProfilePicture() != null && profile.getProfilePicture().length > 0) {
      base64Picture = "data:image/png;base64," + Base64.getEncoder().encodeToString(profile.getProfilePicture());
    }

    UserAttributes attributes = profile.getAttributes();
    if (attributes == null) {
      throw new ResourceNotFoundException("User attributes not found for profile ID: " + profile.getId());
    }

    RecommendedUserDTO dto = new RecommendedUserDTO();
    dto.setUserId(profile.getId());
    dto.setFirstName(profile.getFirst_name());
    dto.setLastName(profile.getLast_name());
    dto.setProfilePicture(base64Picture);
    dto.setHobbies(convertHobbiesToStrings(profile.getHobbies()));
    dto.setAge(getAgeFromBirthDate(attributes.getBirthdate()));
    dto.setGender(attributes.getGender().toString());
    dto.setDistance(calculateDistance(myLocation, attributes.getLocation()));
    dto.setProbability(matchScore);

    return dto;
  }

  /**
   * Calculates the distance between two geographic coordinates using the Haversine formula.
   * @param myLocation User's coordinates [latitude, longitude]
   * @param matchLocation Match's coordinates [latitude, longitude]
   * @return Distance in kilometers, rounded to nearest integer
   */
  private Integer calculateDistance(List<Double> myLocation, List<Double> matchLocation) {
    if (myLocation.size() < 2 || matchLocation.size() < 2) {
      throw new IllegalArgumentException("Location lists must contain at least latitude and longitude");
    }
    double lat1 = myLocation.get(0);
    double lon1 = myLocation.get(1);
    double lat2 = matchLocation.get(0);
    double lon2 = matchLocation.get(1);
    double distance = LocationUtils.calculateDistance(lat1, lon1, lat2, lon2);
    return (int) Math.round(distance);
  }

  private Set<String> convertHobbiesToStrings(Set<Hobby> hobbies) {
    return hobbies.stream()
        .map(Hobby::getName)
        .collect(Collectors.toSet());
  }

  /**
   * Calculates the current age of a user based on their birthdate.
   * @param birthdate User's date of birth
   * @return Calculated age in years
   */
  private Integer getAgeFromBirthDate(LocalDate birthdate) {
    return Period.between(birthdate, LocalDate.now()).getYears();
  }

  /**
   * Retrieves potential matches for a user based on their preferences and attributes,
   * filtering out any users previously dismissed by the requesting user.
   *
   * @param profileId User ID (corresponding to UserProfile ID) to find matches for.
   * @return Map of user IDs to match probability scores for valid, non-dismissed matches, sorted by probability descending, limited to DEFAULT_MAX_RESULTS.
   * @throws ResourceNotFoundException if the user's DatingPool entry is not found.
   */
  public Map<Long, Double> getPossibleMatches(Long profileId) {
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

    return possibleMatches.stream()
            .filter(pool -> !dismissedUserIds.contains(pool.getProfileId()))
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

  /**
   * Calculates the match probability between two users based on their ELO scores and mutual interests.
   * The calculation combines:
   * - Base probability using ELO formula: P(A) = 1 / (1 + 10^((RB - RA)/1071))
   * - Hobby similarity bonus using logarithmic scaling
   * @param userScore User's ELO score
   * @param hobbies User's hobby IDs
   * @param entry Dating pool entry of potential match
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
   * Records that a user has dismissed another user's profile as a potential match recommendation.
   * Once dismissed, the dismissed user should not appear in future recommendations for the initiating user.
   *
   * @param userProfileId The ID of the user profile initiating the dismissal.
   * @param dismissedRecommendationId The ID of the user profile being dismissed.
   * @throws ResourceNotFoundException if either the initiating user's profile or
   * the dismissed user's profile cannot be found by their respective IDs.
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
   * if a specific target user is currently among the calculated top potential matches for the current user.
   *
   * @param currentUserId The ID of the user whose current recommendations should be checked.
   * @param targetUserId The ID of the user to check for presence in the recommendations.
   */
  public boolean isRecommended(Long currentUserId, Long targetUserId) {
    Map<Long, Double> possibleMatches = getPossibleMatches(currentUserId);
    return possibleMatches.containsKey(targetUserId);
  }
}
