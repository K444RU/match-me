package com.matchme.srv.service.type;

import com.matchme.srv.exception.ResourceNotFoundException;
import com.matchme.srv.model.user.profile.user_preferences.PreferenceChangeType;
import com.matchme.srv.repository.PreferenceChangeTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PreferenceChangeTypeService {

  private final PreferenceChangeTypeRepository preferenceChangeTypeRepository;

  public PreferenceChangeType getByName(String name) {
    return preferenceChangeTypeRepository
        .findByName(name)
        .orElseThrow(() -> new ResourceNotFoundException("PreferenceChange"));
  }
}
