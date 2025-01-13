package com.matchme.srv.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.matchme.srv.model.connection.Connection;
import com.matchme.srv.repository.ConnectionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccessValidationService {
    private final ConnectionRepository connectionRepository;

    /**
     * Validate that `currentUserId` can view `targetUserId`. If not, throw an exception (404 or
     * 403).
     */
    public void validateUserAccess(Long currentUserId, Long targetUserId) {
        if (currentUserId.equals(targetUserId)) {
            return;
        }

        List<Connection> connections = connectionRepository.findConnectionsByUserId(currentUserId);
        boolean isConnected = connections.stream().anyMatch(connection -> connection.getUsers()
                .stream().anyMatch(user -> user.getId().equals(targetUserId)));

        if (!isConnected) {
            throw new EntityNotFoundException("User not found or no access rights.");
        }
    }
}
