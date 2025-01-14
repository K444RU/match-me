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
     * Validates if the <code>currentUserId</code> has access to view the <code>targetUserId</code>.
     * If the users are not the same and there is no connection between them, an
     * <code>EntityNotFoundException</code> is thrown indicating that the user is not found or the current user
     * does not have access rights.
     *
     * @param currentUserId
     * @param targetUserId
     * @throws EntityNotFoundException if the current user does not have access to the target user
     */
    public void validateUserAccess(Long currentUserId, Long targetUserId) {
        if (currentUserId.equals(targetUserId)) {
            return;
        }

        boolean isConnected =
                connectionRepository.existsConnectionBetween(currentUserId, targetUserId);

        if (!isConnected) {
            throw new EntityNotFoundException("User not found or no access rights.");
        }
    }
}
