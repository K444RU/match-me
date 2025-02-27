package com.matchme.srv.model.connection;

import org.springframework.stereotype.Service;

import com.matchme.srv.exception.ResourceNotFoundException;
import com.matchme.srv.model.user.profile.user_attributes.UserAttributes;
import com.matchme.srv.repository.MatchingRepository;
import com.matchme.srv.repository.UserAttributesRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DatingPoolSyncService {

    private final MatchingRepository matchingRepository;
    private final UserAttributesRepository userAttributesRepository;

    @Transactional
    public void synchronizeProfile(Long profileId) {

        UserAttributes attributes = userAttributesRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("userAttributes for " + profileId.toString()));

        DatingPool entry = matchingRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("User " + profileId.toString()));

        // update logic here

        matchingRepository.save(entry);
        log.debug("DatingPool synchronized for profile ID: {}", profileId);
    }

}
