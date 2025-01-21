package com.matchme.srv.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.matchme.srv.dto.response.ConnectionResponseDTO;
import com.matchme.srv.dto.response.UserResponseDTO;
import com.matchme.srv.model.connection.Connection;
import com.matchme.srv.model.user.User;
import com.matchme.srv.repository.ConnectionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConnectionService {
    private final ConnectionRepository connectionRepository;

    /**
     * Creates a new connection between two users
     * @param user1
     * @param user2
     * @return The created connection as ConnectionResponseDTO
     * @throws IllegalStateException if connection already exists between users
     * @see ConnectionResponseDTO
     * @see User
     * @see Connection
     */
    public ConnectionResponseDTO createConnection(User user1, User user2) {
        if (connectionRepository.existsConnectionBetween(user1.getId(), user2.getId())) {
            throw new IllegalStateException("Connection already exists between these users");
        }
        
        Connection connection = new Connection();
        Set<User> users = new HashSet<>();
        users.add(user1);
        users.add(user2);
        connection.setUsers(users);
        Connection savedConnection = connectionRepository.save(connection);
        return ConnectionResponseDTO.builder()
                .id(savedConnection.getId())
                .users(savedConnection.getUsers().stream()
                        .map(user -> new UserResponseDTO(user.getId(), user.getEmail(), user.getNumber()))
                        .collect(Collectors.toSet()))
                .build();
    }

    /**
     * Gets all connections for a given user
     * 
     * @param userId
     * @return List of {@link Connection} associated with the user
     */
    public List<Connection> getUserConnections(Long userId) {
        return connectionRepository.findConnectionsByUserId(userId);
    }

    public List<ConnectionResponseDTO> getConnectionResponseDTO(Long currentUserId, Long targetUserId) {
        if (!currentUserId.equals(targetUserId)) {
            throw new EntityNotFoundException("User not found or no access rights.");
        }
        List<Connection> connections = getUserConnections(targetUserId);
        List<ConnectionResponseDTO> connectionResponse = new ArrayList<>();
        for (Connection connection : connections) {
            Set<UserResponseDTO> users = connection.getUsers().stream()
                    .map(u -> new UserResponseDTO(u.getId(), u.getEmail(), u.getNumber()))
                    .collect(Collectors.toSet());
            connectionResponse.add(
                    ConnectionResponseDTO.builder().id(connection.getId()).users(users).build());
        }
        return connectionResponse;
    }

    /**
     * Checks if two users have an established connection.
     * <p>
     * Currently DOES NOT account for inactive connections
     */
    public boolean isConnected(Long requesterId, Long targetId) {
        List<Connection> connections = connectionRepository.findConnectionsByUserId(requesterId);
        for (Connection connection : connections) {
            if (connection.getUsers().stream().anyMatch(user -> user.getId().equals(targetId)))
                return true;
        }
        return false;
    }
}
