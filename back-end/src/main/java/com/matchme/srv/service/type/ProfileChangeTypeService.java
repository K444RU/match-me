package com.matchme.srv.service.type;

import com.matchme.srv.exception.ResourceNotFoundException;
import com.matchme.srv.model.user.profile.ProfileChangeType;
import com.matchme.srv.repository.ProfileChangeTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileChangeTypeService {

  private final ProfileChangeTypeRepository profileChangeTypeRepository;

  public ProfileChangeType getByName(String name) {
    return profileChangeTypeRepository
        .findByName(name)
        .orElseThrow(() -> new ResourceNotFoundException("ProfileChange"));
  }
}
