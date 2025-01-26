package com.matchme.srv.service.type;

import com.matchme.srv.exception.ResourceNotFoundException;
import com.matchme.srv.model.user.activity.ActivityLogType;
import com.matchme.srv.repository.ActivityLogTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActivityLogTypeService {

  private final ActivityLogTypeRepository activityLogTypeRepository;

  public ActivityLogType getByName(String name) {
    return activityLogTypeRepository
        .findByName(name)
        .orElseThrow(() -> new ResourceNotFoundException("Activity"));
  }
}
