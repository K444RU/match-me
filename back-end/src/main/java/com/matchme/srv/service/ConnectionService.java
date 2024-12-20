package com.matchme.srv.service;

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
     * @see ConnectionResponseDTO
     * @see User
     * @see Connection
     */
    public ConnectionResponseDTO createConnection(User user1, User user2) {
        // TODO: Check for existing connection
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
     * @param user
     * @return List of {@link Connection} associated with the user
     * @see User
     */
    public List<Connection> getUserConnections(User user) {
        List<Connection> connections = connectionRepository.findConnectionsByUserId(user.getId());
        return connections;
    }

}
