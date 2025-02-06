package com.matchme.srv.service.type;

import com.matchme.srv.exception.ResourceNotFoundException;
import com.matchme.srv.model.user.UserStateTypes;
import com.matchme.srv.repository.UserStateTypesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserStateTypesService {

  private final UserStateTypesRepository userStateTypesRepository;

  public UserStateTypes getByName(String name) {
    return userStateTypesRepository
        .findByName(name)
        .orElseThrow(() -> new ResourceNotFoundException("UserState"));
  }
}
