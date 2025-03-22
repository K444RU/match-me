package com.matchme.srv.service;

import com.matchme.srv.dto.response.ConnectionsDTO;
import com.matchme.srv.dto.response.MatchingRecommendationsDTO;
import com.matchme.srv.dto.response.MatchingRecommendationsDTO.RecommendedUserDTO;
import com.matchme.srv.exception.PotentialMatchesNotFoundException;
import com.matchme.srv.exception.ResourceNotFoundException;
import com.matchme.srv.model.connection.ConnectionProvider;
import com.matchme.srv.model.connection.DatingPool;
import com.matchme.srv.model.user.profile.Hobby;
import com.matchme.srv.model.user.profile.UserProfile;
import com.matchme.srv.model.user.profile.user_attributes.UserAttributes;
import com.matchme.srv.repository.MatchingRepository;
import com.matchme.srv.repository.UserProfileRepository;
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
   * Minimum probability threshold for considering a match.
   * Matches below this threshold are filtered out.
   */
  private static final double MINIMUM_PROBABILITY = 0.3;

  /**
   * Maximum number of matches to return in a single request.
   */
  private static final int DEFAULT_MAX_RESULTS = 10;

  /**
   * Scaling factor for ELO probability calculation.
   * Affects how much score differences impact match probability.
   */
  private static final double SCALING_FACTOR = 1071.0;

  /**
   * Maximum probability threshold for considering a match.
   * Matches above this threshold are filtered out to prevent overmatching.
   */
  private static final double MAXIMUM_PROBABILITY = 0.91;

  private static final String USER_PROFILE_NOT_FOUND_MESSAGE = "UserProfile for user ";

  private final MatchingRepository matchingRepository;
  private final UserProfileRepository userProfileRepository;
  private final ConnectionService connectionService;

  /**
   * Retrieves and constructs detailed recommendation profiles for potential
   * matches.
   * This method combines the matching algorithm results with user profiles to
   * create
   * a set of 10 recommendations. The process involves:
   * 1. Retrieving the requesting user's profile and location
   * 2. Getting potential matches using the matching algorithm
   * 3. Enriching each match with detailed profile information.
   *
   * @param userId The ID of the user requesting recommendations
   * @return MatchingRecommendationsDTO containing a list of recommended users
   *         with their complete profiles
   * @throws ResourceNotFoundException         if the requesting user's profile or
   *                                           any matched user's profile is not
   *                                           found
   * @throws PotentialMatchesNotFoundException if no potential matches for the
   *                                           user are not found
   */
  public MatchingRecommendationsDTO getRecommendations(Long userId) {

    try {
      UserProfile myProfile = userProfileRepository.findById(userId)
          .orElseThrow(() -> new ResourceNotFoundException(USER_PROFILE_NOT_FOUND_MESSAGE + userId.toString()));

      Long profileId = myProfile.getId();
      List<Double> myLocation = myProfile.getAttributes().getLocation();
      Map<Long, Double> possibleMatches = getPossibleMatches(profileId);

      ConnectionsDTO connections = connectionService.getConnections(profileId);

      Map<Long, String> connectionStatus = new HashMap<>();
      Map<Long, Long> connectionIds = new HashMap<>();

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

      MatchingRecommendationsDTO response = new MatchingRecommendationsDTO();
      List<RecommendedUserDTO> recommendations = new ArrayList<>();

      for (Map.Entry<Long, Double> match : possibleMatches.entrySet()) {
        Long matchUserId = match.getKey();
        Double matchScore = match.getValue();

        UserProfile profile = userProfileRepository.findById(matchUserId)
            .orElseThrow(() -> new ResourceNotFoundException(USER_PROFILE_NOT_FOUND_MESSAGE + matchUserId.toString()));

        if (profile == null) {
          throw new ResourceNotFoundException(USER_PROFILE_NOT_FOUND_MESSAGE + matchUserId.toString());
        }

        String base64Picture = null;
        if (profile != null
            && profile.getProfilePicture() != null
            && profile.getProfilePicture().length > 0) {
          base64Picture = "data:image/png;base64,"
              + Base64.getEncoder().encodeToString(profile.getProfilePicture());
        }

        UserAttributes attributes = profile != null ? profile.getAttributes() : null;
        if (attributes == null) {
            throw new ResourceNotFoundException("User attributes not found for profile ID: " + matchUserId);
        }

        RecommendedUserDTO dto = new RecommendedUserDTO();
        dto.setUserId(matchUserId);
        dto.setFirstName(profile.getFirst_name());
        dto.setLastName(profile.getLast_name());
        dto.setProfilePicture(base64Picture);
        dto.setHobbies(convertHobbiesToStrings(profile.getHobbies()));
        dto.setAge(getAgeFromBirthDate(attributes.getBirthdate()));
        dto.setGender(attributes.getGender().toString());
        dto.setDistance(calculateDistance(myLocation, attributes.getLocation()));
        dto.setProbability(matchScore);

        String status = connectionStatus.getOrDefault(matchUserId, "NONE");
        dto.setConnectionStatus(status);
        if (!"NONE".equals(status)) {
          dto.setConnectionId(connectionIds.get(matchUserId));
        }

        recommendations.add(dto);
      }

      response.setRecommendations(recommendations);
      return response;
    } catch (PotentialMatchesNotFoundException e) {
      throw new PotentialMatchesNotFoundException(
          "No recommendations available for user " + userId + ": " + e.getMessage());
    }
  }

  /**
   * Calculates the distance between two geographic coordinates using the
   * Haversine formula.
   * The formula determines the shortest distance over the earth's surface between
   * two points,
   * giving an 'as-the-crow-flies' distance between the points.
   *
   * @param myLocation    A List containing [latitude, longitude] of the user.
   * @param matchLocation A List containing [latitude, longitude] of the potential
   *                      match.
   * @return The distance between the two points in kilometers, rounded to the
   *         nearest integer.
   * @throws IndexOutOfBoundsException if either location List contains fewer than
   *                                   2 elements
   * @throws IllegalArgumentException  if coordinates are outside valid ranges
   *                                   (latitude: -90 to 90, longitude: -180 to
   *                                   180)
   */
  private Integer calculateDistance(List<Double> myLocation, List<Double> matchLocation) {
    final double EARTH_RADIUS = 6371; // Radius of Earth in kilometers

    double lat1 = Math.toRadians(myLocation.get(0));
    double lon1 = Math.toRadians(myLocation.get(1));
    double lat2 = Math.toRadians(matchLocation.get(0));
    double lon2 = Math.toRadians(matchLocation.get(1));

    double dLat = lat2 - lat1;
    double dLon = lon2 - lon1;

    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
        Math.cos(lat1) * Math.cos(lat2) *
            Math.sin(dLon / 2) * Math.sin(dLon / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

    return (int) (EARTH_RADIUS * c);
  }

  private Set<String> convertHobbiesToStrings(Set<Hobby> hobbies) {
    return hobbies.stream()
        .map(Hobby::getName)
        .collect(Collectors.toSet());
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
   * Retrieves potential matches for a user based on their preferences and
   * attributes.
   * The matching process involves:
   * 1. Retrieving the user's dating pool entry
   * 2. Finding users that match basic criteria (gender, age, location)
   * 3. Calculating match probability based on ELO scores and mutual interests
   * 4. Filtering and sorting matches by probability
   * 
   * @param profileId User ID to find matches for
   * @return Map of user IDs to match probability scores, sorted by probability in
   *         descending order
   * @throws ResourceNotFoundException         if the user is not found
   * @throws PotentialMatchesNotFoundException if no compatible matches are found
   *                                           within acceptable probability range
   */
  public Map<Long, Double> getPossibleMatches(Long profileId) {

    // get the users datingPool entry
    DatingPool entry = matchingRepository.findById(profileId)
        .orElseThrow(() -> new ResourceNotFoundException(USER_PROFILE_NOT_FOUND_MESSAGE + profileId.toString()));

    // find users that match parameters
    List<DatingPool> possibleMatches = matchingRepository.findUsersThatMatchParameters(entry.getLookingForGender(),
        entry.getMyGender(), entry.getMyAge(), entry.getAgeMin(), entry.getAgeMax(),
        entry.getSuitableGeoHashes(), entry.getMyLocation(), 3);

    if (possibleMatches.isEmpty()) {
      throw new PotentialMatchesNotFoundException(
          "Potential matches with selected parameters for user " + profileId.toString());
    }
    // Calculate match probability, filter and sort
    Map<Long, Double> bestMatches = possibleMatches.stream()
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

    if (bestMatches.isEmpty()) {
      throw new PotentialMatchesNotFoundException(
          "Potential matches within acceptable probability range for user " + profileId.toString());
    }

    return bestMatches;
  }

  /**
   * Calculates the match probability between two users based on their ELO scores
   * and mutual interests.
   * The calculation combines:
   * - Base probability using ELO formula: P(A) = 1 / (1 + 10^((RB - RA)/1071))
   * - Hobby similarity bonus using logarithmic scaling
   *
   * @param userScore The ELO score of the requesting user
   * @param hobbies   Set of hobby IDs for the requesting user
   * @param entry     Dating pool entry of the potential match
   * @return Calculated match probability between 0.0 and 1.0
   */
  private Double calculateProbability(Integer userScore, Set<Long> hobbies, DatingPool entry) {
    Double probability = 1.0 / (1.0 + Math.pow(10, (userScore - entry.getActualScore()) / SCALING_FACTOR));

    int mutualhobbies = 0;

    for (Long hobby : hobbies) {
      if (entry.getHobbyIds().contains(hobby)) {
        mutualhobbies++;
      }
    }

    if (mutualhobbies != 0) {
      probability += (0.2 * (Math.log((double) mutualhobbies + 1) / Math.log((double) hobbies.size() + 1)));
    }

    return probability;
  }
}
