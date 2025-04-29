package com.matchme.srv.service.user;

import com.matchme.srv.dto.response.*;
import com.matchme.srv.mapper.UserParametersMapper;
import com.matchme.srv.model.connection.ConnectionProvider;
import com.matchme.srv.model.user.User;
import com.matchme.srv.model.user.UserAuth;
import com.matchme.srv.model.user.profile.UserProfile;
import com.matchme.srv.model.user.profile.Hobby;
import com.matchme.srv.model.user.profile.user_attributes.UserAttributes;
import com.matchme.srv.model.user.profile.user_preferences.UserPreferences;
import com.matchme.srv.repository.UserProfileRepository;
import com.matchme.srv.repository.UserRepository;
import com.matchme.srv.service.AccessValidationService;
import com.matchme.srv.mapper.user.UserDTOMapper;
import com.matchme.srv.service.ConnectionService;
import com.matchme.srv.util.LocationUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserQueryService {

  private final UserRepository userRepository;
  private final UserProfileRepository userProfileRepository;

  private final ConnectionService connectionService;
  private final AccessValidationService accessValidationService;

  private final UserDTOMapper userDtoMapper;
  private final UserParametersMapper parametersMapper;

  private static final String USER_NOT_FOUND_MESSAGE = "User not found!";
  private static final String PROFILE_NOT_FOUND_MESSAGE = "Profile not found!";

  /**
   * Return a user’s basic info, first checks whether the current user can access the target’s data.
   */
  public CurrentUserResponseDTO getCurrentUserDTO(Long currentUserId, Long targetUserId) {
    accessValidationService.validateUserAccess(currentUserId, targetUserId);
    User user =
        userRepository
            .findById(targetUserId)
            .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MESSAGE));
    boolean isOwner = currentUserId.equals(targetUserId);
    return userDtoMapper.toCurrentUserResponseDTO(user, isOwner);
  }

  public UserParametersResponseDTO getParameters(Long userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MESSAGE));
    UserAuth auth = user.getUserAuth();
    UserProfile profile = user.getProfile();

    if (profile == null) {
      throw new EntityNotFoundException(PROFILE_NOT_FOUND_MESSAGE);
    }

    UserAttributes attributes = profile.getAttributes();
    UserPreferences preferences = profile.getPreferences();

    return parametersMapper.toUserParametersDTO(user, attributes, preferences, auth);
  }

  public User getUser(Long userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MESSAGE));
  }

  public User getUserByEmail(String email) {
    return userRepository
        .findByEmail(email)
        .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MESSAGE));
  }

  public ProfileResponseDTO getUserProfileDTO(Long currentUserId, Long targetUserId) {
    accessValidationService.validateUserAccess(currentUserId, targetUserId);

    User user = userRepository
        .findById(targetUserId)
        .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MESSAGE));

    UserProfile profile = user.getProfile();
    if (profile == null) {
        throw new EntityNotFoundException(PROFILE_NOT_FOUND_MESSAGE);
    }

    return userDtoMapper.toProfileResponseDTO(profile);
  }

  public BiographicalResponseDTO getBiographicalResponseDTO(Long currentUserId, Long targetUserId) {
    accessValidationService.validateUserAccess(currentUserId, targetUserId);

    UserProfile profile =
        userRepository
            .findById(targetUserId)
            .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MESSAGE))
            .getProfile();
    if (profile == null) throw new EntityNotFoundException(PROFILE_NOT_FOUND_MESSAGE);

    return userDtoMapper.tobBiographicalResponseDTO(profile);
  }

  public SettingsResponseDTO getSettingsResponseDTO(Long currentUserId, Long targetUserId) {
    accessValidationService.validateUserAccess(currentUserId, targetUserId);
    UserParametersResponseDTO parameters = getParameters(targetUserId);
    return userDtoMapper.toSettingsResponseDTO(parameters);
  }

  public BatchUserResponseDTO getUsersBatch(Long currentUserId, List<Long> userIds) {
    UserProfile currentUserProfile = userProfileRepository.findById(currentUserId)
            .orElseThrow(() -> new EntityNotFoundException(PROFILE_NOT_FOUND_MESSAGE + currentUserId));
    List<Double> myLocation = currentUserProfile.getAttributes().getLocation();

    ConnectionsDTO connections = connectionService.getConnections(currentUserId);
    Map<Long, String> connectionStatus = new HashMap<>();
    Map<Long, Long> connectionIds = new HashMap<>();
    populateConnectionMaps(connections, connectionStatus, connectionIds);

    Map<Long, Double> userIdMap = userIds.stream()
            .collect(Collectors.toMap(
                    id -> id,
                    id -> 0.0,
                    (e1, e2) -> e1,
                    LinkedHashMap::new
            ));

    List<RecommendedUserDTO> recommendations = buildRecommendations(userIdMap, myLocation, connectionStatus, connectionIds, currentUserId);

    BatchUserResponseDTO response = new BatchUserResponseDTO();
    response.setUsers(recommendations);
    return response;
  }

  /**
   * Helper method to build a list of RecommendedUserDTOs from user IDs.
   *
   * @param userIdMap        Map of user IDs to probabilities (or 0.0 for batch)
   * @param myLocation       Current user's location for distance calculation
   * @param connectionStatus Map of user IDs to connection status
   * @param connectionIds    Map of user IDs to connection IDs
   * @param currentUserId    ID of the current user for access validation
   * @return List of populated RecommendedUserDTOs
   */
  private List<RecommendedUserDTO> buildRecommendations(Map<Long, Double> userIdMap, List<Double> myLocation,
                                                        Map<Long, String> connectionStatus, Map<Long, Long> connectionIds,
                                                        Long currentUserId) {
    List<RecommendedUserDTO> recommendations = new ArrayList<>();

    List<UserProfile> profiles = userProfileRepository.findAllById(userIdMap.keySet());
    Map<Long, UserProfile> profileMap = profiles.stream()
            .collect(Collectors.toMap(UserProfile::getId, p -> p));

    for (Map.Entry<Long, Double> entry : userIdMap.entrySet()) {
      Long userId = entry.getKey();
      Double probability = entry.getValue();

      UserProfile profile = profileMap.get(userId);
      if (profile == null) {
        continue; // Skip missing profiles instead of throwing an exception
      }

      try {
        accessValidationService.validateUserAccess(currentUserId, userId);
        RecommendedUserDTO dto = createRecommendedUserDTO(profile, probability, myLocation);
        setConnectionInfo(dto, userId, connectionStatus, connectionIds);
        recommendations.add(dto);
      } catch (EntityNotFoundException e) {
        // Skip users without access
        continue;
      }
    }

    return recommendations;
  }

  /**
   * Helper method to create a RecommendedUserDTO from profile data and probability.
   *
   * @param profile     UserProfile of the matched user
   * @param probability Probability score of the match (or 0.0 for batch)
   * @param myLocation  Current user's location for distance calculation
   * @return Populated RecommendedUserDTO
   */
  private RecommendedUserDTO createRecommendedUserDTO(UserProfile profile, Double probability, List<Double> myLocation) {
    String base64Picture = null;
    if (profile.getProfilePicture() != null && profile.getProfilePicture().length > 0) {
      base64Picture = "data:image/png;base64," + Base64.getEncoder().encodeToString(profile.getProfilePicture());
    }

    UserAttributes attributes = profile.getAttributes();
    if (attributes == null) {
      throw new EntityNotFoundException("User attributes not found for profile ID: " + profile.getId());
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
    dto.setProbability(probability);

    return dto;
  }

  /**
   * Calculates the distance between two geographic coordinates using the Haversine formula.
   *
   * @param myLocation    User's coordinates [latitude, longitude]
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

  /**
   * Converts a set of Hobby entities to a set of hobby names.
   *
   * @param hobbies Set of Hobby entities
   * @return Set of hobby names
   */
  private Set<String> convertHobbiesToStrings(Set<Hobby> hobbies) {
    return hobbies.stream()
            .map(Hobby::getName)
            .collect(Collectors.toSet());
  }

  /**
   * Calculates the current age of a user based on their birthdate.
   *
   * @param birthdate User's date of birth
   * @return Calculated age in years
   */
  private Integer getAgeFromBirthDate(LocalDate birthdate) {
    return Period.between(birthdate, LocalDate.now()).getYears();
  }

  /**
   * Helper method to populate connection status and ID maps from ConnectionsDTO.
   *
   * @param connections      DTO containing connection information
   * @param connectionStatus Map to store user ID to connection status
   * @param connectionIds    Map to store user ID to connection ID
   */
  private void populateConnectionMaps(ConnectionsDTO connections, Map<Long, String> connectionStatus,
                                      Map<Long, Long> connectionIds) {
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
   * Helper method to set connection status and ID on the RecommendedUserDTO.
   *
   * @param dto              RecommendedUserDTO to update
   * @param matchUserId      ID of the matched user
   * @param connectionStatus Map of user IDs to connection status
   * @param connectionIds    Map of user IDs to connection IDs
   */
  private void setConnectionInfo(RecommendedUserDTO dto, Long matchUserId, Map<Long, String> connectionStatus,
                                 Map<Long, Long> connectionIds) {
    String status = connectionStatus.getOrDefault(matchUserId, "NONE");
    dto.setConnectionStatus(status);
    if (!"NONE".equals(status)) {
      dto.setConnectionId(connectionIds.get(matchUserId));
    }
  }
}
