package com.matchme.srv.service;

import com.matchme.srv.TestDataFactory;
import com.matchme.srv.dto.response.ConnectionsDTO;
import com.matchme.srv.model.connection.Connection;
import com.matchme.srv.model.connection.ConnectionState;
import com.matchme.srv.model.enums.ConnectionStatus;
import com.matchme.srv.model.user.User;
import com.matchme.srv.repository.ConnectionRepository;
import com.matchme.srv.repository.UserRepository;
import com.matchme.srv.service.user.UserScoreService;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.matchme.srv.TestDataFactory.DEFAULT_CONNECTION_ID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ConnectionServiceTest {

  @Mock
  private ConnectionRepository connectionRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserScoreService userScoreService;

  @InjectMocks
  private ConnectionService connectionService;

  private User requester;
  private User target;
  private Connection connection;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    requester = TestDataFactory.createUser(TestDataFactory.DEFAULT_USER_ID, TestDataFactory.DEFAULT_EMAIL);
    target = TestDataFactory.createUser(TestDataFactory.DEFAULT_TARGET_USER_ID, TestDataFactory.DEFAULT_TARGET_EMAIL);

    connection = Connection.builder()
        .id(1L)
        .users(new HashSet<>(Set.of(requester, target)))
        .connectionStates(new HashSet<>())
        .build();
  }

  @Test
  void getUserConnections_success() {
    ConnectionState acceptedState = createState(ConnectionStatus.ACCEPTED, requester.getId(), target.getId());
    connection.getConnectionStates().add(acceptedState);
    when(connectionRepository.findConnectionsByUserId(requester.getId())).thenReturn(List.of(connection));

    ConnectionsDTO result = connectionService.getConnections(requester.getId());

    assertEquals(1, result.getActive().size(), "There should be one active connection");
    assertEquals(target.getId(), result.getActive().getFirst().getUserId(),
        "The active connection should be with the target user");
    assertEquals(connection.getId(), result.getActive().getFirst().getConnectionId(), "The connection ID should match");
    assertTrue(result.getPendingIncoming().isEmpty(), "There should be no pending incoming requests");
    assertTrue(result.getPendingOutgoing().isEmpty(), "There should be no pending outgoing requests");

    verify(connectionRepository).findConnectionsByUserId(requester.getId());
  }

  @Test
  void sendConnectionRequest_success_newConnection() {
    when(userRepository.findById(requester.getId())).thenReturn(Optional.of(requester));
    when(userRepository.findById(target.getId())).thenReturn(Optional.of(target));
    when(connectionRepository.findConnectionBetween(requester.getId(), target.getId())).thenReturn(null);
    when(connectionRepository.save(any(Connection.class))).thenReturn(connection);

    connectionService.sendConnectionRequest(requester.getId(), target.getId());

    verify(connectionRepository).save(any(Connection.class));
  }

  @Test
  void sendConnectionRequest_toSelf_throwsIllegalStateException() {
    IllegalStateException exception = assertThrows(IllegalStateException.class,
        () -> connectionService.sendConnectionRequest(requester.getId(), requester.getId()));
    assertEquals("Cannot send a connection request to yourself", exception.getMessage());
  }

  @Test
  void sendConnectionRequest_pendingExists_throwsIllegalStateException() {
    ConnectionState state = createState(ConnectionStatus.PENDING, requester.getId(), target.getId());
    connection.getConnectionStates().add(state);
    when(connectionRepository.findConnectionBetween(requester.getId(), target.getId())).thenReturn(connection);

    IllegalStateException exception = assertThrows(IllegalStateException.class,
        () -> connectionService.sendConnectionRequest(requester.getId(), target.getId()));
    assertEquals("A pending request already exists from you to this user", exception.getMessage());
  }

  @Test
  void sendConnectionRequest_alreadyAccepted_throwsIllegalStateException() {
    ConnectionState state = createState(ConnectionStatus.ACCEPTED, requester.getId(), target.getId());
    connection.getConnectionStates().add(state);
    when(connectionRepository.findConnectionBetween(requester.getId(), target.getId())).thenReturn(connection);

    IllegalStateException exception = assertThrows(IllegalStateException.class,
        () -> connectionService.sendConnectionRequest(requester.getId(), target.getId()));
    assertEquals("You are already connected with this user", exception.getMessage());
  }

  @Test
  void acceptConnectionRequest_success() {
    ConnectionState pendingState = createState(ConnectionStatus.PENDING, requester.getId(), target.getId());
    connection.getConnectionStates().add(pendingState);
    when(connectionRepository.findByIdWithUsers(1L)).thenReturn(Optional.of(connection));
    when(userRepository.findById(target.getId())).thenReturn(Optional.of(target));
    when(connectionRepository.save(any(Connection.class))).thenReturn(connection);

    connectionService.acceptConnectionRequest(requester.getId(), target.getId());

    verify(connectionRepository).save(any(Connection.class));
  }

  @Test
  void acceptConnectionRequest_notPending_throwsIllegalStateException() {
    ConnectionState acceptedState = createState(ConnectionStatus.ACCEPTED, requester.getId(), target.getId());
    connection.getConnectionStates().add(acceptedState);
    when(connectionRepository.findByIdWithUsers(DEFAULT_CONNECTION_ID)).thenReturn(Optional.of(connection));

    IllegalStateException exception = assertThrows(IllegalStateException.class,
        () -> connectionService.acceptConnectionRequest(DEFAULT_CONNECTION_ID, target.getId()));
    assertEquals("Connection is not in PENDING state", exception.getMessage());
  }

  @Test
  void acceptConnectionRequest_notAuthorized_throwsIllegalStateException() {
    ConnectionState pendingState = createState(ConnectionStatus.PENDING, requester.getId(), target.getId());
    connection.getConnectionStates().add(pendingState);
    when(connectionRepository.findByIdWithUsers(DEFAULT_CONNECTION_ID)).thenReturn(Optional.of(connection));

    IllegalStateException exception = assertThrows(IllegalStateException.class,
        () -> connectionService.acceptConnectionRequest(DEFAULT_CONNECTION_ID, requester.getId()));
    assertEquals("You are not authorized to accept this request", exception.getMessage());
  }

  @Test
  void acceptConnectionRequest_connectionNotFound_throwsEntityNotFoundException() {
    when(connectionRepository.findById(1L)).thenReturn(Optional.empty());

    EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
        () -> connectionService.acceptConnectionRequest(1L, target.getId()));
    assertEquals("Connection not found", exception.getMessage());
  }

  @Test
  void rejectConnectionRequest_success() {
    ConnectionState pendingState = createState(ConnectionStatus.PENDING, requester.getId(), target.getId());
    connection.getConnectionStates().add(pendingState);
    when(connectionRepository.findByIdWithUsers(1L)).thenReturn(Optional.of(connection));
    when(userRepository.findById(target.getId())).thenReturn(Optional.of(target));
    when(connectionRepository.save(any(Connection.class))).thenReturn(connection);

    connectionService.rejectConnectionRequest(DEFAULT_CONNECTION_ID, target.getId());

    verify(connectionRepository).save(any(Connection.class));
  }

  @Test
  void rejectConnectionRequest_notPending_throwsIllegalStateException() {
    ConnectionState acceptedState = createState(ConnectionStatus.ACCEPTED, requester.getId(), target.getId());
    connection.getConnectionStates().add(acceptedState);
    when(connectionRepository.findByIdWithUsers(DEFAULT_CONNECTION_ID)).thenReturn(Optional.of(connection));

    IllegalStateException exception = assertThrows(IllegalStateException.class,
        () -> connectionService.rejectConnectionRequest(DEFAULT_CONNECTION_ID, target.getId()));
    assertEquals("Connection is not in PENDING state", exception.getMessage());
  }

  @Test
  void rejectConnectionRequest_notAuthorized_throwsIllegalStateException() {
    ConnectionState pendingState = createState(ConnectionStatus.PENDING, requester.getId(), target.getId());
    connection.getConnectionStates().add(pendingState);
    when(connectionRepository.findByIdWithUsers(DEFAULT_CONNECTION_ID)).thenReturn(Optional.of(connection));

    IllegalStateException exception = assertThrows(IllegalStateException.class,
        () -> connectionService.rejectConnectionRequest(DEFAULT_CONNECTION_ID, requester.getId()));
    assertEquals("You are not authorized to reject this request", exception.getMessage());
  }

  @Test
  void disconnect_success() {
    ConnectionState acceptedState = createState(ConnectionStatus.ACCEPTED, requester.getId(), target.getId());
    connection.getConnectionStates().add(acceptedState);
    when(connectionRepository.findById(1L)).thenReturn(Optional.of(connection));
    when(userRepository.findById(requester.getId())).thenReturn(Optional.of(requester));
    when(connectionRepository.save(any(Connection.class))).thenReturn(connection);

    connectionService.disconnect(DEFAULT_CONNECTION_ID, requester.getId());

    verify(connectionRepository).save(any(Connection.class));
  }

  @Test
  void disconnect_notPartOfConnection_throwsIllegalStateException() {
    User outsider = TestDataFactory.createUser(3L, "outsider@example.com");
    ConnectionState acceptedState = createState(ConnectionStatus.ACCEPTED, requester.getId(), target.getId());
    connection.getConnectionStates().add(acceptedState);
    when(connectionRepository.findById(1L)).thenReturn(Optional.of(connection));

    IllegalStateException exception = assertThrows(IllegalStateException.class,
        () -> connectionService.disconnect(DEFAULT_CONNECTION_ID, outsider.getId()));
    assertEquals("You are not part of this connection", exception.getMessage());
  }

  @Test
  void disconnect_notAccepted_throwsIllegalStateException() {
    ConnectionState pendingState = createState(ConnectionStatus.PENDING, requester.getId(), target.getId());
    connection.getConnectionStates().add(pendingState);
    when(connectionRepository.findById(1L)).thenReturn(Optional.of(connection));

    IllegalStateException exception = assertThrows(IllegalStateException.class,
        () -> connectionService.disconnect(DEFAULT_CONNECTION_ID, requester.getId()));
    assertEquals("Connection is not in ACCEPTED state", exception.getMessage());
  }

  @Test
  void getConnections_success() {
    ConnectionState acceptedState = createState(ConnectionStatus.ACCEPTED, requester.getId(), target.getId());
    connection.getConnectionStates().add(acceptedState);
    when(connectionRepository.findConnectionsByUserId(requester.getId())).thenReturn(List.of(connection));

    ConnectionsDTO result = connectionService.getConnections(requester.getId());

    assertEquals(1, result.getActive().size());
    assertEquals(target.getId(), result.getActive().getFirst().getUserId());
    assertEquals(connection.getId(), result.getActive().getFirst().getConnectionId());
    assertTrue(result.getPendingIncoming().isEmpty());
    assertTrue(result.getPendingOutgoing().isEmpty());
    verify(connectionRepository).findConnectionsByUserId(requester.getId());
  }

  @Test
  void getConnections_pendingOutgoing() {
    ConnectionState pendingState = createState(ConnectionStatus.PENDING, requester.getId(), target.getId());
    connection.getConnectionStates().add(pendingState);
    when(connectionRepository.findConnectionsByUserId(requester.getId())).thenReturn(List.of(connection));

    ConnectionsDTO result = connectionService.getConnections(requester.getId());

    assertTrue(result.getActive().isEmpty());
    assertTrue(result.getPendingIncoming().isEmpty());
    assertEquals(1, result.getPendingOutgoing().size());
    assertEquals(target.getId(), result.getPendingOutgoing().getFirst().getUserId());
    assertEquals(connection.getId(), result.getPendingOutgoing().getFirst().getConnectionId());
  }

  @Test
  void getConnections_pendingIncoming() {
    ConnectionState pendingState = createState(ConnectionStatus.PENDING, target.getId(), requester.getId());
    connection.getConnectionStates().add(pendingState);
    when(connectionRepository.findConnectionsByUserId(requester.getId())).thenReturn(List.of(connection));

    ConnectionsDTO result = connectionService.getConnections(requester.getId());

    assertTrue(result.getActive().isEmpty());
    assertEquals(1, result.getPendingIncoming().size());
    assertEquals(target.getId(), result.getPendingIncoming().getFirst().getUserId());
    assertEquals(connection.getId(), result.getPendingIncoming().getFirst().getConnectionId());
    assertTrue(result.getPendingOutgoing().isEmpty());
  }

  private ConnectionState createState(ConnectionStatus status, Long requesterId, Long targetId) {
    ConnectionState state = new ConnectionState();
    state.setConnection(connection);
    state.setUser(requester);
    state.setStatus(status);
    state.setRequesterId(requesterId);
    state.setTargetId(targetId);
    state.setTimestamp(LocalDateTime.now());
    return state;
  }
}
