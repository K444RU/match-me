package com.matchme.srv.controller;

import com.matchme.srv.dto.graphql.BioWrapper;
import com.matchme.srv.dto.graphql.ProfileWrapper;
import com.matchme.srv.dto.graphql.SettingsWrapper;
import com.matchme.srv.dto.graphql.UserGraphqlDTO;
import com.matchme.srv.dto.graphql.UserInputs.AccountSettingsInput;
import com.matchme.srv.dto.graphql.UserInputs.AttributesSettingsInput;
import com.matchme.srv.dto.graphql.UserInputs.PreferencesSettingsInput;
import com.matchme.srv.dto.graphql.UserInputs.ProfilePictureInput;
import com.matchme.srv.dto.graphql.UserInputs.ProfileSettingsInput;
import com.matchme.srv.dto.graphql.UserInputs.UserParametersInput;
import com.matchme.srv.dto.request.UserParametersRequestDTO;
import com.matchme.srv.dto.request.settings.AccountSettingsRequestDTO;
import com.matchme.srv.dto.request.settings.AttributesSettingsRequestDTO;
import com.matchme.srv.dto.request.settings.PreferencesSettingsRequestDTO;
import com.matchme.srv.dto.request.settings.ProfilePictureSettingsRequestDTO;
import com.matchme.srv.dto.request.settings.ProfileSettingsRequestDTO;
import com.matchme.srv.dto.response.BatchUserResponseDTO;
import com.matchme.srv.dto.response.SettingsResponseDTO;
import com.matchme.srv.mapper.GraphqlDtoMapper;
import com.matchme.srv.model.user.User;
import com.matchme.srv.security.jwt.SecurityUtils;
import com.matchme.srv.service.user.UserCreationService;
import com.matchme.srv.service.user.UserProfileService;
import com.matchme.srv.service.user.UserQueryService;
import com.matchme.srv.service.user.UserSettingsService;
import java.util.List;
import java.util.stream.Collectors;
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
public class UserGraphqlController {

  private final UserQueryService userQueryService;
  private final SecurityUtils securityUtils;
  private final UserCreationService creationService;
  private final UserSettingsService settingsService;
  private final UserProfileService profileService;
  private final GraphqlDtoMapper mapper;

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

  @QueryMapping
  public SettingsWrapper mySettings(Authentication authentication) {
    if (authentication == null) {
      return null;
    }
    try {
      Long currentUserId = securityUtils.getCurrentUserId(authentication);
      SettingsResponseDTO settings =
          userQueryService.getSettingsResponseDTO(currentUserId, currentUserId);
      return new SettingsWrapper(settings);
    } catch (Exception e) {
      log.error("Error fetching current user settings: {}", e.getMessage());
      return null;
    }
  }

  /** GraphQL Query Resolver for fetching multiple users by their IDs. */
  @QueryMapping
  public BatchUserResponseDTO usersBatch(
      @Argument List<String> userIds, Authentication authentication) {
    if (authentication == null) {
      return null;
    }
    try {
      Long currentUserId = securityUtils.getCurrentUserId(authentication);
      List<Long> userIdsLong = userIds.stream().map(Long::parseLong).collect(Collectors.toList());
      return userQueryService.getUsersBatch(currentUserId, userIdsLong);
    } catch (Exception e) {
      log.error("Error fetching users batch: {}", e.getMessage());
      return null;
    }
  }

  /** GraphQL Mutation Resolver for completing user registration with parameters. */
  @MutationMapping
  public Boolean completeRegistration(
      @Argument("input") UserParametersInput input, Authentication authentication) {
    if (authentication == null) {
      return false;
    }
    try {
      Long currentUserId = securityUtils.getCurrentUserId(authentication);
      UserParametersRequestDTO parameters = mapper.toUserParametersRequestDTO(input);
      creationService.setUserParameters(currentUserId, parameters);
      return true;
    } catch (Exception e) {
      log.error("Error completing registration: {}", e.getMessage(), e);
      return false;
    }
  }

  /** GraphQL Mutation Resolver for updating account settings. */
  @MutationMapping
  public Boolean updateAccountSettings(
      @Argument("input") AccountSettingsInput input, Authentication authentication) {
    if (authentication == null) {
      return false;
    }
    try {
      Long currentUserId = securityUtils.getCurrentUserId(authentication);
      AccountSettingsRequestDTO settings = mapper.toAccountSettingsRequestDTO(input);
      settingsService.updateAccountSettings(currentUserId, settings);
      return true;
    } catch (Exception e) {
      log.error("Error updating account settings: {}", e.getMessage(), e);
      return false;
    }
  }

  /** GraphQL Mutation Resolver for updating profile settings. */
  @MutationMapping
  public Boolean updateProfileSettings(
      @Argument("input") ProfileSettingsInput input, Authentication authentication) {
    if (authentication == null) {
      return false;
    }
    try {
      Long currentUserId = securityUtils.getCurrentUserId(authentication);
      ProfileSettingsRequestDTO settings = mapper.toProfileSettingsRequestDTO(input);
      settingsService.updateProfileSettings(currentUserId, settings);
      return true;
    } catch (Exception e) {
      log.error("Error updating profile settings: {}", e.getMessage(), e);
      return false;
    }
  }

  /** GraphQL Mutation Resolver for updating user attributes settings. */
  @MutationMapping
  public Boolean updateAttributesSettings(
      @Argument("input") AttributesSettingsInput input, Authentication authentication) {
    if (authentication == null) {
      return false;
    }
    try {
      Long currentUserId = securityUtils.getCurrentUserId(authentication);
      AttributesSettingsRequestDTO settings = mapper.toAttributesSettingsRequestDTO(input);
      settingsService.updateAttributesSettings(currentUserId, settings);
      return true;
    } catch (Exception e) {
      log.error("Error updating attributes settings: {}", e.getMessage(), e);
      return false;
    }
  }

  /** GraphQL Mutation Resolver for updating user preferences settings. */
  @MutationMapping
  public Boolean updatePreferencesSettings(
      @Argument("input") PreferencesSettingsInput input, Authentication authentication) {
    if (authentication == null) {
      return false;
    }
    try {
      Long currentUserId = securityUtils.getCurrentUserId(authentication);
      PreferencesSettingsRequestDTO settings = mapper.toPreferencesSettingsRequestDTO(input);
      settingsService.updatePreferencesSettings(currentUserId, settings);
      return true;
    } catch (Exception e) {
      log.error("Error updating preferences settings: {}", e.getMessage(), e);
      return false;
    }
  }

  /** GraphQL Mutation Resolver for uploading a profile picture. */
  @MutationMapping
  public Boolean uploadProfilePicture(
      @Argument("input") ProfilePictureInput input, Authentication authentication) {
    if (authentication == null) {
      return false;
    }
    try {
      Long currentUserId = securityUtils.getCurrentUserId(authentication);
      ProfilePictureSettingsRequestDTO request = mapper.toProfilePictureSettingsRequestDTO(input);
      profileService.saveProfilePicture(currentUserId, request);
      return true;
    } catch (Exception e) {
      log.error("Error uploading profile picture: {}", e.getMessage(), e);
      return false;
    }
  }
}
