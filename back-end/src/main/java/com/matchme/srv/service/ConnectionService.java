package com.matchme.srv.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import com.matchme.srv.dto.response.ConnectionsDTO;
import com.matchme.srv.model.connection.Connection;
import com.matchme.srv.model.connection.ConnectionProvider;
import com.matchme.srv.model.connection.ConnectionState;
import com.matchme.srv.model.enums.ConnectionStatus;
import com.matchme.srv.model.user.User;
import com.matchme.srv.repository.ConnectionRepository;
import com.matchme.srv.repository.UserRepository;
import com.matchme.srv.service.user.UserScoreService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConnectionService {
  private final ConnectionRepository connectionRepository;
  private final UserRepository userRepository;
  private final UserScoreService userScoreService;

  private static final String CONNECTION_NOT_FOUND = "Connection not found";

  /**
   * Gets all connections for a user, splitting them into active, pending
   * incoming, and pending outgoing lists.
   *
   * This method fetches all connections for the given user and organizes them
   * based on their current state:
   * - Active: Connections that are accepted.
   * - Pending Incoming: Requests sent to this user that are still pending.
   * - Pending Outgoing: Requests this user sent that are still pending.
   *
   * @param currentUserId The ID of the user whose connections we want to fetch.
   * @return A ConnectionsDTO with lists of user IDs for each connection type.
   */
  public ConnectionsDTO getConnections(Long currentUserId) {
    List<Connection> connections = connectionRepository.findConnectionsByUserId(currentUserId);
    List<ConnectionProvider> active = new ArrayList<>();
    List<ConnectionProvider> pendingIncoming = new ArrayList<>();
    List<ConnectionProvider> pendingOutgoing = new ArrayList<>();

    for (Connection connection : connections) {
      ConnectionState latestState = connection.getConnectionStates().stream()
              .max(Comparator.comparing(ConnectionState::getTimestamp))
              .orElse(null);

      if (latestState == null) {
        continue;
      }

      Long otherUserId = connection.getUsers().stream()
          .map(User::getId)
          .filter(id -> !id.equals(currentUserId))
          .findFirst()
          .orElse(null);

      if (otherUserId == null) {
        continue;
      }

      ConnectionProvider connectionInformation = new ConnectionProvider(connection.getId(), otherUserId);

      switch (latestState.getStatus()) {
        case ACCEPTED:
          active.add(connectionInformation);
          break;
        case PENDING:
          if (latestState.getRequesterId() != null && latestState.getRequesterId().equals(currentUserId)) {
            pendingOutgoing.add(connectionInformation);
          } else {
            pendingIncoming.add(connectionInformation);
          }
          break;
        case REJECTED, DISCONNECTED:
          break;
        default:
          break;
      }
    }
    return new ConnectionsDTO(active, pendingIncoming, pendingOutgoing);
  }

  /**
   * Sends a connection request from one user to another.
   *
   * Checks if a connection already exists:
   * - If it’s pending and from the same requester, throws an error.
   * - If it’s accepted, throws an error.
   * - If it’s rejected or disconnected, adds a new pending state.
   * - If no connection exists, creates a new one with a pending state.
   *
   * @param requesterId The ID of the user sending the request.
   * @param targetId    The ID of the user receiving the request.
   * @throws IllegalStateException If the request is invalid (e.g., to self or
   *                               duplicate).
   */
  @Transactional(readOnly = false)
  public Long sendConnectionRequest(Long requesterId, Long targetId) {
    if (requesterId.equals(targetId)) {
      throw new IllegalStateException("Cannot send a connection request to yourself");
    }

    Connection existingConnection = connectionRepository.findConnectionBetween(requesterId, targetId);
    if (existingConnection != null) {
      ConnectionState currentState = getCurrentState(existingConnection);
      if (currentState != null) {
        switch (currentState.getStatus()) {
          case PENDING -> {
            if (currentState.getRequesterId().equals(requesterId)) {
              throw new IllegalStateException("A pending request already exists from you to this user");
            }
          }
          case ACCEPTED -> throw new IllegalStateException("You are already connected with this user");
          case REJECTED, DISCONNECTED -> {
            addNewState(existingConnection, ConnectionStatus.PENDING, requesterId, targetId, requesterId);
            return existingConnection.getId();
          }
        }
      } else {
        addNewState(existingConnection, ConnectionStatus.PENDING, requesterId, targetId, requesterId);
        return existingConnection.getId();
      }
    }
    existingConnection = createPendingConnection(requesterId, targetId);
    return existingConnection.getId();
  }

  /**
   * Accepts a pending connection request, marking it as accepted.
   *
   * Only the target of the request can accept it, and it must be in a pending
   * state.
   * 
   * Calls userScoreService to update request senders score based on decision.
   *
   * @param connectionId The ID of the connection to accept.
   * @param acceptorId   The ID of the user accepting the request.
   * @throws EntityNotFoundException If the connection doesn’t exist.
   * @throws IllegalStateException   If the connection isn’t pending or the user
   *                                 can’t accept it.
   */
  @Transactional
  public Connection acceptConnectionRequest(Long connectionId, Long acceptorId) {
    Connection connection = connectionRepository.findByIdWithUsers(connectionId)
            .orElseThrow(() -> new EntityNotFoundException(CONNECTION_NOT_FOUND));

    ConnectionState currentState = getCurrentState(connection);

    if (currentState == null || currentState.getStatus() != ConnectionStatus.PENDING) {
      throw new IllegalStateException("Connection is not in PENDING state");
    }
    if (!currentState.getTargetId().equals(acceptorId)) {
      throw new IllegalStateException("You are not authorized to accept this request");
    }

    addNewState(connection, ConnectionStatus.ACCEPTED, currentState.getRequesterId(), acceptorId, acceptorId);

    userScoreService.updateUserScore(acceptorId, currentState.getRequesterId(), true);
    return connection;
  }

  /**
   * Rejects a pending connection request, marking it as rejected.
   *
   * Only the target of the request can reject it, and it must be in a pending
   * state.
   * 
   * Calls userScoreService to update request senders score based on decision.
   *
   * @param connectionId The ID of the connection to reject.
   * @param rejectorId   The ID of the user rejecting the request.
   * @throws EntityNotFoundException If the connection doesn’t exist.
   * @throws IllegalStateException   If the connection isn’t pending or the user
   *                                 can’t reject it.
   */
  @Transactional(readOnly = false)
  public Connection rejectConnectionRequest(Long connectionId, Long rejectorId) {
    Connection connection = connectionRepository.findByIdWithUsers(connectionId)
        .orElseThrow(() -> new EntityNotFoundException(CONNECTION_NOT_FOUND));
    ConnectionState currentState = getCurrentState(connection);

    if (currentState == null || currentState.getStatus() != ConnectionStatus.PENDING) {
      throw new IllegalStateException("Connection is not in PENDING state");
    }
    if (!currentState.getTargetId().equals(rejectorId)) {
      throw new IllegalStateException("You are not authorized to reject this request");
    }

    addNewState(connection, ConnectionStatus.REJECTED, currentState.getRequesterId(), rejectorId, rejectorId);

    userScoreService.updateUserScore(rejectorId, currentState.getRequesterId(), false);
    return connection;
  }

  /**
   * Disconnects an accepted connection, marking it as disconnected.
   *
   * Any user in the connection can disconnect it, but it must be in an accepted
   * state.
   *
   * @param connectionId The ID of the connection to disconnect.
   * @param userId       The ID of the user disconnecting it.
   * @throws EntityNotFoundException If the connection doesn’t exist.
   * @throws IllegalStateException   If the connection isn’t accepted or the user
   *                                 isn’t part of it.
   */
  @Transactional(readOnly = false)
  public Connection disconnect(Long connectionId, Long userId) {
    Connection connection = connectionRepository.findById(connectionId)
        .orElseThrow(() -> new EntityNotFoundException(CONNECTION_NOT_FOUND));
    if (!connection.getUsers().stream().anyMatch(u -> u.getId().equals(userId))) {
      throw new IllegalStateException("You are not part of this connection");
    }
    ConnectionState currentState = getCurrentState(connection);
    if (currentState == null || currentState.getStatus() != ConnectionStatus.ACCEPTED) {
      throw new IllegalStateException("Connection is not in ACCEPTED state");
    }

    addNewState(connection, ConnectionStatus.DISCONNECTED, null, null, userId);
    return connection;
  }

  /**
   * Helper method that creates a new connection with a pending state between two
   * users.
   *
   * @param requesterId The ID of the user sending the request.
   * @param targetId    The ID of the user receiving the request.
   */
  private Connection createPendingConnection(Long requesterId, Long targetId) {
    User requester = userRepository.findById(requesterId)
        .orElseThrow(() -> new EntityNotFoundException("Requester not found"));
    User target = userRepository.findById(targetId)
        .orElseThrow(() -> new EntityNotFoundException("Target not found"));

    Connection connection = Connection.builder()
        .users(Set.of(requester, target))
        .build();
    ConnectionState state = ConnectionState.builder()
        .connection(connection)
        .user(requester)
        .status(ConnectionStatus.PENDING)
        .requesterId(requesterId)
        .targetId(targetId)
        .timestamp(LocalDateTime.now())
        .build();
    connection.getConnectionStates().add(state);

    connectionRepository.save(connection);
    return connection;
  }

  /**
   * Helper method that adds a new state to an existing connection (e.g.,
   * ACCEPTED, REJECTED).
   *
   * @param connection   The connection to update.
   * @param status       The new status to set (e.g., ACCEPTED, REJECTED).
   * @param requesterId  The ID of the original requester (can be null for
   *                     DISCONNECTED).
   * @param targetId     The ID of the original target (can be null for
   *                     DISCONNECTED).
   * @param actingUserId The ID of the user making this change.
   */
  private void addNewState(Connection connection, ConnectionStatus status, Long requesterId, Long targetId,
      Long actingUserId) {
    User actingUser = userRepository.findById(actingUserId)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));
    ConnectionState newState = ConnectionState.builder()
        .connection(connection)
        .user(actingUser)
        .status(status)
        .requesterId(requesterId)
        .targetId(targetId)
        .timestamp(LocalDateTime.now())
        .build();
    connection.getConnectionStates().add(newState);
    connectionRepository.save(connection);
  }

  /**
   * Helper method that finds the latest state (accepted or pending) for a
   * connection.
   *
   * Only considers ACCEPTED or PENDING states as they define the current status.
   *
   * @param connection The connection to check.
   * @return The most recent ACCEPTED or PENDING state, or null if none exist.
   */
  public ConnectionState getCurrentState(Connection connection) {
    return connection.getConnectionStates().stream()
        .filter(
            state -> state.getStatus() == ConnectionStatus.ACCEPTED || state.getStatus() == ConnectionStatus.PENDING)
        .max(Comparator.comparing(ConnectionState::getTimestamp))
        .orElse(null);
  }
}
