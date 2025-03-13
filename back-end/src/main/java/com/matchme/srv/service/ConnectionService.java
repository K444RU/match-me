package com.matchme.srv.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.matchme.srv.dto.response.ConnectionsDTO;
import com.matchme.srv.model.connection.ConnectionState;
import com.matchme.srv.model.enums.ConnectionStatus;
import com.matchme.srv.repository.UserRepository;
import org.springframework.stereotype.Service;

import com.matchme.srv.dto.response.ConnectionResponseDTO;
import com.matchme.srv.dto.response.UserResponseDTO;
import com.matchme.srv.model.connection.Connection;
import com.matchme.srv.model.user.User;
import com.matchme.srv.repository.ConnectionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConnectionService {
    private final ConnectionRepository connectionRepository;
    private final UserRepository userRepository;

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

        Connection connection = Connection.builder()
                .users(Set.of(user1, user2))
                .build();
        ConnectionState state = new ConnectionState();
        state.setConnection(connection);
        state.setStatus(ConnectionStatus.ACCEPTED); // Default to ACCEPTED for now
        state.setRequesterId(user1.getId());
        state.setTargetId(user2.getId());
        connection.getConnectionStates().add(state);

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
        return connectionRepository.existsConnectionBetween(requesterId, targetId);
    }

    /**
     * Sends a connection request from requester to target, creating a PENDING state.
     * Prevents duplicates by checking existing connections and their states.
     */
    @Transactional(readOnly = false)
    public void sendConnectionRequest(Long requesterId, Long targetId) {
        if (requesterId.equals(targetId)) {
            throw new IllegalStateException("Cannot send a connection request to yourself");
        }

        Connection existingConnection = connectionRepository.findConnectionBetween(requesterId, targetId);
        if (existingConnection != null) {
            ConnectionState currentState = getCurrentState(existingConnection);
            switch (currentState.getStatus()) {
                case PENDING:
                    if (currentState.getRequesterId().equals(requesterId)) {
                        throw new IllegalStateException("A pending request already exists from you to this user");
                    }
                    // If the target has a pending request to the requester, still create a new PENDING request
                    // User must explicitly accept via the accept endpoint
                    break;
                case ACCEPTED:
                    throw new IllegalStateException("You are already connected with this user");
                case REJECTED:
                case DISCONNECTED:
                    // Allow a new request by adding a PENDING state
                    addNewState(existingConnection, ConnectionStatus.PENDING, requesterId, targetId, requesterId);
                    return;
            }
        }

        // No existing connection or PENDING from the other user, create a new one with PENDING state
        createPendingConnection(requesterId, targetId);
    }

    /**
     * Accepts a pending connection request, updating the state to ACCEPTED.
     */
    @Transactional(readOnly = false)
    public void acceptConnectionRequest(Long connectionId, Long acceptorId) {
        Connection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new EntityNotFoundException("Connection not found"));
        ConnectionState currentState = getCurrentState(connection);

        if (currentState.getStatus() != ConnectionStatus.PENDING) {
            throw new IllegalStateException("Connection is not in PENDING state");
        }
        if (!currentState.getTargetId().equals(acceptorId)) {
            throw new IllegalStateException("You are not authorized to accept this request");
        }

        addNewState(connection, ConnectionStatus.ACCEPTED, currentState.getRequesterId(), acceptorId, acceptorId);
    }

    /**
     * Disconnects an accepted connection, updating the state to DISCONNECTED.
     */
    @Transactional(readOnly = false)
    public void rejectConnectionRequest(Long connectionId, Long rejectorId) {
        Connection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new EntityNotFoundException("Connection not found"));
        ConnectionState currentState = getCurrentState(connection);

        if (currentState.getStatus() != ConnectionStatus.PENDING) {
            throw new IllegalStateException("Connection is not in PENDING state");
        }
        if (!currentState.getTargetId().equals(rejectorId)) {
            throw new IllegalStateException("You are not authorized to reject this request");
        }

        addNewState(connection, ConnectionStatus.REJECTED, currentState.getRequesterId(), rejectorId, rejectorId);
    }

    /**
     * Disconnects an accepted connection, updating the state to DISCONNECTED.
     */
    @Transactional(readOnly = false)
    public void disconnect(Long connectionId, Long userId) {
        Connection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new EntityNotFoundException("Connection not found"));
        if (!connection.getUsers().stream().anyMatch(u -> u.getId().equals(userId))) {
            throw new IllegalStateException("You are not part of this connection");
        }
        ConnectionState currentState = getCurrentState(connection);
        if (currentState.getStatus() != ConnectionStatus.ACCEPTED) {
            throw new IllegalStateException("Connection is not in ACCEPTED state");
        }

        addNewState(connection, ConnectionStatus.DISCONNECTED, null, null, userId);
    }

    public ConnectionsDTO getConnections(Long currentUserId) {
        List<Connection> connections = connectionRepository.findConnectionsByUserId(currentUserId);
        System.out.println("Fetched " + connections.size() + " connections for user " + currentUserId);

        for (Connection connection : connections) {
            System.out.println("Connection ID: " + connection.getId() + ", States: " + connection.getConnectionStates().size());
        }
        List<Long> active = new ArrayList<>();
        List<Long> pendingIncoming = new ArrayList<>();
        List<Long> pendingOutgoing = new ArrayList<>();

        for (Connection connection : connections) {
            ConnectionState currentState = getCurrentState(connection);
            if (currentState == null) {
                continue;
            }
            Long otherUserId = connection.getUsers().stream()
                    .map(User::getId)
                    .filter(id -> !id.equals(currentUserId))
                    .findFirst()
                    .orElse(null);
            if (otherUserId != null) {
                switch (currentState.getStatus()) {
                    case ACCEPTED:
                        active.add(otherUserId);
                        break;
                    case PENDING:
                        if (currentState.getRequesterId().equals(currentUserId)) {
                            pendingOutgoing.add(otherUserId);
                        } else {
                            pendingIncoming.add(otherUserId);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        return new ConnectionsDTO(active, pendingIncoming, pendingOutgoing);
    }

    private void createPendingConnection(Long requesterId, Long targetId) {
        User requester = userRepository.findById(requesterId).orElseThrow(EntityNotFoundException::new);
        User target = userRepository.findById(targetId).orElseThrow(EntityNotFoundException::new);

        Connection connection = Connection.builder()
                .users(Set.of(requester, target))
                .build();
        ConnectionState state = new ConnectionState();
        state.setConnection(connection);
        state.setUser(requester);
        state.setStatus(ConnectionStatus.PENDING);
        state.setRequesterId(requesterId);
        state.setTargetId(targetId);
        state.setTimestamp(LocalDateTime.now());
        connection.getConnectionStates().add(state);

        connectionRepository.save(connection);

    }

    private void addNewState(Connection connection, ConnectionStatus status, Long requesterId, Long targetId, Long actingUserId) {
        User actingUser = userRepository.findById(actingUserId).orElseThrow(EntityNotFoundException::new);
        ConnectionState newState = new ConnectionState();
        newState.setConnection(connection);
        newState.setUser(actingUser);
        newState.setStatus(status);
        newState.setRequesterId(requesterId);
        newState.setTargetId(targetId);
        newState.setTimestamp(LocalDateTime.now());
        connection.getConnectionStates().add(newState);
        connectionRepository.save(connection);

    }

    private ConnectionState getCurrentState(Connection connection) {
        Set<ConnectionState> states = connection.getConnectionStates();
        if (states.isEmpty()) {
            System.out.println("Warning: No states found for connection ID: " + connection.getId());
            return null;
        }
        return new ArrayList<>(states).stream()
                .max(Comparator.comparing(ConnectionState::getTimestamp))
                .orElseThrow(() -> new IllegalStateException("Connection not found"));
    }

}
