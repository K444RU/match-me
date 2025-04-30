package com.matchme.srv.controller;

import com.matchme.srv.dto.graphql.UserGraphqlDTO;
import com.matchme.srv.dto.response.ConnectionsDTO;
import com.matchme.srv.dto.response.MatchingRecommendationsDTO;
import com.matchme.srv.model.connection.ConnectionProvider;
import com.matchme.srv.model.user.User;
import com.matchme.srv.security.jwt.SecurityUtils;
import com.matchme.srv.service.ConnectionService;
import com.matchme.srv.service.MatchingService;
import com.matchme.srv.service.user.UserQueryService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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


}
