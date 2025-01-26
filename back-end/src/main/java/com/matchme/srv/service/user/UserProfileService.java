package com.matchme.srv.service.user;

import com.matchme.srv.dto.request.settings.ProfilePictureSettingsRequestDTO;

public interface UserProfileService {
  void saveProfilePicture(Long userId, ProfilePictureSettingsRequestDTO request);
}
