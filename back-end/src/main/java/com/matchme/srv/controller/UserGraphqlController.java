package com.matchme.srv.controller;

import com.matchme.srv.dto.graphql.BioWrapper;
import com.matchme.srv.dto.graphql.ProfileWrapper;
import com.matchme.srv.dto.graphql.UserGraphqlDTO;
import com.matchme.srv.model.user.User;
import com.matchme.srv.security.jwt.SecurityUtils;
import com.matchme.srv.service.user.UserQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserGraphqlController {

  private final UserQueryService userQueryService;
  private final SecurityUtils securityUtils;

  /**
   * GraphQL Query Resolver for fetching a user by ID. Maps to the 'user(id: ID!)' query in
   * schema.graphqls.
   */
  @QueryMapping
  public UserGraphqlDTO user(@Argument String id, Authentication authentication) {
    try {
      Long userId = Long.parseLong(id);
      Long currentUserId = securityUtils.getCurrentUserId(authentication);
      // Check if current user has access to this user
      userQueryService.getCurrentUserDTO(currentUserId, userId);
      User userEntity = userQueryService.getUser(userId);
      return userEntity != null ? new UserGraphqlDTO(userEntity) : null;
    } catch (Exception e) {
      log.error("Error fetching user: {}", e.getMessage());
      return null;
    }
  }

  /** GraphQL Query Resolver for fetching the profile of a user by ID. */
  @QueryMapping
  public ProfileWrapper profile(@Argument String id, Authentication authentication) {
    try {
      Long userId = Long.parseLong(id);
      Long currentUserId = securityUtils.getCurrentUserId(authentication);
      // Check if current user has access to this profile
      userQueryService.getUserProfileDTO(currentUserId, userId);
      User user = userQueryService.getUser(userId);
      return new ProfileWrapper(user);
    } catch (Exception e) {
      log.error("Error fetching profile: {}", e.getMessage());
      return null;
    }
  }

  /** GraphQL Query Resolver for fetching the bio of a user by ID. */
  @QueryMapping
  public BioWrapper bio(@Argument String id, Authentication authentication) {
    try {
      Long userId = Long.parseLong(id);
      Long currentUserId = securityUtils.getCurrentUserId(authentication);
      // Check if current user has access to this bio
      userQueryService.getBiographicalResponseDTO(currentUserId, userId);
      User user = userQueryService.getUser(userId);
      return new BioWrapper(user);
    } catch (Exception e) {
      log.error("Error fetching bio: {}", e.getMessage());
      return null;
    }
  }

  /** GraphQL Query Resolver for fetching the current authenticated user. */
  @QueryMapping
  public User me(Authentication authentication) {
    if (authentication == null) {
      return null;
    }
    try {
      Long currentUserId = securityUtils.getCurrentUserId(authentication);
      return userQueryService.getUser(currentUserId);
    } catch (Exception e) {
      log.error("Error fetching current user: {}", e.getMessage());
      return null;
    }
  }

  /** GraphQL Query Resolver for fetching the profile of the current authenticated user. */
  @QueryMapping
  public ProfileWrapper myProfile(Authentication authentication) {
    if (authentication == null) {
      return null;
    }
    try {
      Long currentUserId = securityUtils.getCurrentUserId(authentication);
      User user = userQueryService.getUser(currentUserId);
      return new ProfileWrapper(user);
    } catch (Exception e) {
      log.error("Error fetching current user profile: {}", e.getMessage());
      return null;
    }
  }

  /** GraphQL Query Resolver for fetching the bio of the current authenticated user. */
  @QueryMapping
  public BioWrapper myBio(Authentication authentication) {
    if (authentication == null) {
      return null;
    }
    try {
      Long currentUserId = securityUtils.getCurrentUserId(authentication);
      User user = userQueryService.getUser(currentUserId);
      return new BioWrapper(user);
    } catch (Exception e) {
      log.error("Error fetching current user bio: {}", e.getMessage());
      return null;
    }
  }
}
