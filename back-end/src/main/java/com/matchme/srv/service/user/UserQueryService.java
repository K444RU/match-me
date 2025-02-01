package com.matchme.srv.service.user;

import com.matchme.srv.dto.response.BiographicalResponseDTO;
import com.matchme.srv.dto.response.CurrentUserResponseDTO;
import com.matchme.srv.dto.response.ProfileResponseDTO;
import com.matchme.srv.dto.response.SettingsResponseDTO;
import com.matchme.srv.dto.response.UserParametersResponseDTO;
import com.matchme.srv.mapper.UserParametersMapper;
import com.matchme.srv.model.user.User;
import com.matchme.srv.model.user.UserAuth;
import com.matchme.srv.model.user.profile.UserProfile;
import com.matchme.srv.model.user.profile.user_attributes.UserAttributes;
import com.matchme.srv.model.user.profile.user_preferences.UserPreferences;
import com.matchme.srv.repository.UserRepository;
import com.matchme.srv.service.AccessValidationService;
import com.matchme.srv.service.user.dto.UserDTOMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserQueryService {

  private final UserRepository userRepository;

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
    return userDtoMapper.toCurrentUserResponseDTO(user);
  }

  public UserParametersResponseDTO getParameters(Long userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MESSAGE));
    UserAuth auth = user.getUserAuth();
    UserProfile profile = user.getProfile();
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
}
