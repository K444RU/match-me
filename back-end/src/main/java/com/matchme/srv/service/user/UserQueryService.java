package com.matchme.srv.service.user;

import com.matchme.srv.dto.response.BiographicalResponseDTO;
import com.matchme.srv.dto.response.CurrentUserResponseDTO;
import com.matchme.srv.dto.response.ProfileResponseDTO;
import com.matchme.srv.dto.response.SettingsResponseDTO;
import com.matchme.srv.dto.response.UserParametersResponseDTO;
import com.matchme.srv.model.user.User;

public interface UserQueryService {
  UserParametersResponseDTO getParameters(Long userId);

  User getUser(Long userId);

  User getUserByEmail(String email);

  CurrentUserResponseDTO getCurrentUserDTO(Long userId, Long targetUserId);

  ProfileResponseDTO getUserProfileDTO(Long currentUserId, Long targetUserId);

  BiographicalResponseDTO getBiographicalResponseDTO(Long currentUserId, Long targetUserId);

  SettingsResponseDTO getSettingsResponseDTO(Long currentUserId, Long targetUserId);
}
