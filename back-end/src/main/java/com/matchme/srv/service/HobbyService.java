package com.matchme.srv.service;

import org.springframework.stereotype.Service;
import com.matchme.srv.model.user.profile.Hobby;
import com.matchme.srv.repository.HobbyRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HobbyService {

    private final HobbyRepository hobbyRepository;

    public Hobby findById(Long id) {
        return hobbyRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Hobby not found with id: " + id));
    }

}
