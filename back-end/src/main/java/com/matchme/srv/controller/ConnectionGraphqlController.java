package com.matchme.srv.controller;

import static com.matchme.srv.model.connection.ConnectionUpdateType.*;

import com.matchme.srv.dto.graphql.ConnectionUpdateEvent;
import com.matchme.srv.dto.graphql.UserGraphqlDTO;
import com.matchme.srv.dto.response.ConnectionsDTO;
import com.matchme.srv.dto.response.MatchingRecommendationsDTO;
import com.matchme.srv.model.connection.Connection;
import com.matchme.srv.model.connection.ConnectionProvider;
import com.matchme.srv.model.connection.ConnectionUpdateMessage;
import com.matchme.srv.model.user.User;
import com.matchme.srv.publisher.ConnectionPublisher;
import com.matchme.srv.security.jwt.SecurityUtils;
import com.matchme.srv.service.ConnectionService;
import com.matchme.srv.service.MatchingService;
import com.matchme.srv.service.user.UserQueryService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ConnectionGraphqlController {

  private final MatchingService matchingService;
  private final ConnectionService connectionService;
  private final UserQueryService userQueryService;
  private final SecurityUtils securityUtils;
  private final ConnectionPublisher connectionPublisher;

  private static final String INVALID_CONNECTION = "INVALID_CONNECTION";

  /** GraphQL Query Resolver for fetching recommendations for the authenticated user. */
  @QueryMapping
  public List<UserGraphqlDTO> recommendations(Authentication authentication) {
    if (authentication == null) {
      return List.of();
    }
    try {
      Long currentUserId = securityUtils.getCurrentUserId(authentication);
      MatchingRecommendationsDTO recommendationsDTO =
          matchingService.getRecommendations(currentUserId);
      return recommendationsDTO.getRecommendations().stream()
          .map(
              id -> {
                try {
                  User user = userQueryService.getUser(id);
                  return user != null ? new UserGraphqlDTO(user) : null;
                } catch (EntityNotFoundException e) {
                  log.warn("User with ID {} not found while fetching recommendations.", id);
                  return null;
                }
              })
          .filter(Objects::nonNull)
          .toList();
    } catch (Exception e) {
      log.error("Error fetching recommendations: {}", e.getMessage(), e);
      return List.of();
    }
  }

  /** GraphQL Query Resolver for fetching connections for the authenticated user. */
  @QueryMapping
  public List<UserGraphqlDTO> connections(Authentication authentication) {
    if (authentication == null) {
      return List.of();
    }
    try {
      Long currentUserId = securityUtils.getCurrentUserId(authentication);
      ConnectionsDTO connectionsDTO = connectionService.getConnections(currentUserId);
      return connectionsDTO.getActive().stream()
          .map(ConnectionProvider::getUserId)
          .map(
              id -> {
                try {
                  User user = userQueryService.getUser(id);
                  return user != null ? new UserGraphqlDTO(user) : null;
                } catch (EntityNotFoundException e) {
                  log.warn("User with ID {} not found while fetching connections.", id);
                  return null;
                }
              })
          .filter(Objects::nonNull)
          .toList();
    } catch (Exception e) {
      log.error("Error fetching connections: {}", e.getMessage(), e);
      return List.of();
    }
  }

  //   @MessageMapping("/connection.sendRequest")
  @MutationMapping
  public ConnectionUpdateEvent sendConnectionRequest(@Argument Long targetUserId, Authentication authentication) {
    log.info("Received connection request for user: " + targetUserId);
    Long senderId = securityUtils.getCurrentUserId(authentication);
    Long connectionId = connectionService.sendConnectionRequest(senderId, targetUserId);

    User otherUser = userQueryService.getUser(targetUserId);
    UserGraphqlDTO userDTO = new UserGraphqlDTO(otherUser);

    ConnectionUpdateEvent event = new ConnectionUpdateEvent(
        REQUEST_SENT,
        connectionId.toString(),
        userDTO
    );

    connectionPublisher.publishUpdate(
        targetUserId,
        new ConnectionUpdateMessage(NEW_REQUEST, new ConnectionProvider(connectionId, senderId)));
    log.info("NEW_REQUEST: Sent connection request to user: " + targetUserId);

    connectionPublisher.publishUpdate(
        senderId,
        new ConnectionUpdateMessage(REQUEST_SENT, new ConnectionProvider(connectionId, targetUserId)));
    log.info("REQUEST_SENT: Sent connection request to user: " + targetUserId);

    return event;
  }

  // @MessageMapping("/connection.acceptRequest")
  @MutationMapping
  public ConnectionUpdateEvent acceptConnectionRequest(@Argument Long connectionId, Authentication authentication) {
    Long acceptorId = securityUtils.getCurrentUserId(authentication);
    Connection connection = connectionService.acceptConnectionRequest(connectionId, acceptorId);

    Long otherUserId =
        connection.getUsers().stream()
            .map(User::getId)
            .filter(id -> !id.equals(acceptorId))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException(INVALID_CONNECTION));

    User otherUser = userQueryService.getUser(otherUserId);
    UserGraphqlDTO userDTO = new UserGraphqlDTO(otherUser);

    ConnectionUpdateEvent event = new ConnectionUpdateEvent(
        REQUEST_ACCEPTED,
        connectionId.toString(),
        userDTO
    );

    connectionPublisher.publishUpdate(
        acceptorId,
        new ConnectionUpdateMessage(REQUEST_ACCEPTED, new ConnectionProvider(connectionId, otherUserId)));
    log.info("REQUEST_ACCEPTED: Sent connection request to user: " + otherUserId);

    connectionPublisher.publishUpdate(
        otherUserId,
        new ConnectionUpdateMessage(REQUEST_ACCEPTED, new ConnectionProvider(connectionId, acceptorId)));
    log.info("REQUEST_ACCEPTED: Sent connection request to user: " + acceptorId);

    return event;
  }

  // @MessageMapping("/connection.rejectRequest")
  @MutationMapping
  public ConnectionUpdateEvent rejectConnectionRequest(@Argument Long connectionId, Authentication authentication) {
    Long rejectorId = securityUtils.getCurrentUserId(authentication);
    Connection connection = connectionService.rejectConnectionRequest(connectionId, rejectorId);

    Long otherUserId =
        connection.getUsers().stream()
            .map(User::getId)
            .filter(id -> !id.equals(rejectorId))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException(INVALID_CONNECTION));

    User otherUser = userQueryService.getUser(otherUserId);
    UserGraphqlDTO userDTO = new UserGraphqlDTO(otherUser);

    ConnectionUpdateEvent event = new ConnectionUpdateEvent(
        REQUEST_REJECTED,
        connectionId.toString(),
        userDTO
    );

    connectionPublisher.publishUpdate(
        rejectorId,
        new ConnectionUpdateMessage(REQUEST_REJECTED, new ConnectionProvider(connectionId, otherUserId)));
    log.info("REQUEST_REJECTED: Sent connection request to user: " + otherUserId);

    connectionPublisher.publishUpdate(
        otherUserId,
        new ConnectionUpdateMessage(REQUEST_REJECTED, new ConnectionProvider(connectionId, rejectorId)));
    log.info("REQUEST_REJECTED: Sent connection request to user: " + rejectorId);

    return event;
  }

  // @MessageMapping("/connection.disconnect")
  @MutationMapping
  public ConnectionUpdateEvent disconnect(@Argument Long connectionId, Authentication authentication) {
    Long userId = securityUtils.getCurrentUserId(authentication);
    Connection connection = connectionService.disconnect(connectionId, userId);

    Long otherUserId =
        connection.getUsers().stream()
            .map(User::getId)
            .filter(id -> !id.equals(userId))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException(INVALID_CONNECTION));

    User otherUser = userQueryService.getUser(otherUserId);
    UserGraphqlDTO userDTO = new UserGraphqlDTO(otherUser);

    ConnectionUpdateEvent event = new ConnectionUpdateEvent(
        DISCONNECTED,
        connectionId.toString(),
        userDTO
    );

    connectionPublisher.publishUpdate(
        userId,
        new ConnectionUpdateMessage(DISCONNECTED, new ConnectionProvider(connectionId, otherUserId)));
    log.info("DISCONNECTED: Sent connection request to user: " + otherUserId);

    connectionPublisher.publishUpdate(
        otherUserId,
        new ConnectionUpdateMessage(DISCONNECTED, new ConnectionProvider(connectionId, userId)));
    log.info("DISCONNECTED: Sent connection request to user: " + userId);

    return event;
  }
}
