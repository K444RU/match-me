package com.matchme.srv.service;

import com.matchme.srv.exception.ResourceNotFoundException;
import com.matchme.srv.model.user.profile.Hobby;
import com.matchme.srv.repository.HobbyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HobbyService {

  private final HobbyRepository hobbyRepository;

  public Hobby getById(Long id) {
    return hobbyRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Hobby"));
  }
}
