package com.matchme.srv.service.user;

import com.matchme.srv.dto.request.settings.AccountSettingsRequestDTO;
import com.matchme.srv.dto.request.settings.AttributesSettingsRequestDTO;
import com.matchme.srv.dto.request.settings.PreferencesSettingsRequestDTO;
import com.matchme.srv.dto.request.settings.ProfileSettingsRequestDTO;

public interface UserSettingsService {
  void updateAccountSettings(Long userId, AccountSettingsRequestDTO settings);

  void updateProfileSettings(Long userId, ProfileSettingsRequestDTO settings);

  void updateAttributesSettings(Long userId, AttributesSettingsRequestDTO settings);

  void updatePreferencesSettings(Long userId, PreferencesSettingsRequestDTO settings);
}
