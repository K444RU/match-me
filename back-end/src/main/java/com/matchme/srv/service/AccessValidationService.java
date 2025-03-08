package com.matchme.srv.service;

import org.springframework.stereotype.Service;
import com.matchme.srv.repository.ConnectionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccessValidationService {
    private final ConnectionRepository connectionRepository;

    /**
     * Validates if currentUserId has access to view targetUserId's profile.
     * Access is granted if:
     * - currentUserId is the owner (same as targetUserId).
     * - Users are connected (ACCEPTED connection exists).
     * - There’s an outstanding (PENDING) connection request between them.
     * - targetUserId is in currentUserId’s recommendations (stub for now).
     *
     * @param currentUserId The ID of the requesting user
     * @param targetUserId  The ID of the user whose profile is being accessed
     * @throws EntityNotFoundException if access is denied
     */
    public void validateUserAccess(Long currentUserId, Long targetUserId) {
        if (currentUserId.equals(targetUserId)) {
            return; // Owner always has access
        }
        if (connectionRepository.existsConnectionBetween(currentUserId, targetUserId)) {
            return; // Accepted connection grants access
        }
        if (connectionRepository.hasPendingConnectionRequest(currentUserId, targetUserId) ||
                connectionRepository.hasPendingConnectionRequest(targetUserId, currentUserId)) {
            return; // Pending request (either direction) grants access
        }
        if (connectionRepository.isInRecommendations(currentUserId, targetUserId)) {
            return; //TODO: NB!!Recommendation grants access (stub temporary solution for now)
        }
        throw new EntityNotFoundException("User not found or no access rights.");
    }
}
