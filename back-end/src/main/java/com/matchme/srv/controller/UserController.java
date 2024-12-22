package com.matchme.srv.controller;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.matchme.srv.dto.request.settings.AccountSettingsRequestDTO;
import com.matchme.srv.dto.request.settings.AttributesSettingsRequestDTO;
import com.matchme.srv.dto.request.settings.PreferencesSettingsRequestDTO;
import com.matchme.srv.dto.request.settings.ProfileSettingsRequestDTO;
import com.matchme.srv.dto.request.UserParametersRequestDTO;
import com.matchme.srv.dto.response.BiographicalResponseDTO;
import com.matchme.srv.dto.response.ConnectionResponseDTO;
import com.matchme.srv.dto.response.CurrentUserResponseDTO;
import com.matchme.srv.dto.response.GenderTypeDTO;
import com.matchme.srv.dto.response.ProfileResponseDTO;
import com.matchme.srv.dto.response.SettingsResponseDTO;
import com.matchme.srv.dto.response.UserParametersResponseDTO;
import com.matchme.srv.dto.response.UserResponseDTO;
import com.matchme.srv.model.connection.Connection;
import com.matchme.srv.model.user.User;
import com.matchme.srv.model.user.profile.UserProfile;
import com.matchme.srv.security.services.UserDetailsImpl;
import com.matchme.srv.service.ConnectionService;
import com.matchme.srv.service.UserService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users")
public class UserController {

  @Autowired
  private UserService userService;

  @Autowired
  private ConnectionService connectionService;

  /**
   * Retrieves a users basic information by ID
   * <p>
   * Checks if the requester is connected or is the user.
   * 
   * @param targetId       ID of the user to retrieve
   * @param authentication
   * @return ID, email, first_name, last_name, alias and roles
   * @see CurrentUserResponseDTO
   */
  @GetMapping("/{targetId}")
  public ResponseEntity<CurrentUserResponseDTO> getUser(@PathVariable Long targetId, Authentication authentication) {
    UserDetailsImpl requesterUserDetails = (UserDetailsImpl) authentication.getPrincipal();
    Long requesterUserId = requesterUserDetails.getId();

    if (requesterUserId == targetId || userService.isConnected(requesterUserId, targetId)) {
      UserProfile userProfile = userService.getUserProfile(targetId);
      User user = userService.getUser(targetId);

      CurrentUserResponseDTO currentUser = CurrentUserResponseDTO.builder()
          .id(targetId)
          .email(user.getEmail())
          .firstName(userProfile != null ? userProfile.getFirst_name() : null)
          .lastName(userProfile != null ? userProfile.getLast_name() : null)
          .alias(userProfile != null ? userProfile.getAlias() : null)
          .role(user.getRoles())
          .build();

      return ResponseEntity.ok(currentUser);
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

  /**
   * Retrieves a users profile
   * <p>
   * Checks if the requester is connected or is the user.
   * 
   * @param targetId       ID of the user to retrieve profile for
   * @param authentication
   * @return first_name, last_name and city
   * @see ProfileResponseDTO
   */
  @GetMapping("/{targetId}/profile")
  public ResponseEntity<ProfileResponseDTO> getProfile(@PathVariable Long targetId, Authentication authentication) {
    UserDetailsImpl requesterUserDetails = (UserDetailsImpl) authentication.getPrincipal();
    Long requesterUserId = requesterUserDetails.getId();

    if (requesterUserId == targetId || userService.isConnected(requesterUserId, targetId)) {
      UserProfile userProfile = userService.getUserProfile(targetId);
      ProfileResponseDTO profile = ProfileResponseDTO.builder()
          .first_name(userProfile.getFirst_name())
          .last_name(userProfile.getLast_name())
          .city(userProfile.getCity())
          .build();
      return ResponseEntity.ok(profile);
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

  }

  /**
   * Retrieves a users biographical data
   * <p>
   * Checks if the requester is connected or is the user
   * 
   * @param targetId       ID of the user to retrieve biographical data for
   * @param authentication
   * @return User age, preferences and attributes
   * @see BiographicalResponseDTO
   */
  @GetMapping("/{targetId}/bio")
  public ResponseEntity<BiographicalResponseDTO> getBio(@PathVariable Long targetId, Authentication authentication) {
    UserDetailsImpl requesterUserDetails = (UserDetailsImpl) authentication.getPrincipal();
    Long requesterUserId = requesterUserDetails.getId();

    if (requesterUserId == targetId || userService.isConnected(requesterUserId, targetId)) {
      UserProfile userProfile = userService.getUserProfile(targetId);

      BiographicalResponseDTO bio = BiographicalResponseDTO.builder()
          .gender_self(new GenderTypeDTO(userProfile.getAttributes().getGender().getId(),
              userProfile.getAttributes().getGender().getName()))
          .gender_other(new GenderTypeDTO(userProfile.getPreferences().getGender().getId(),
              userProfile.getPreferences().getGender().getName()))
          .age_self(Period.between(userProfile.getAttributes().getBirth_date(),
              LocalDate.now()).getYears())
          .age_min(userProfile.getPreferences().getAge_min())
          .age_max(userProfile.getPreferences().getAge_max())
          .distance(userProfile.getPreferences().getDistance())
          .probability_tolerance(userProfile.getPreferences().getProbability_tolerance())
          .build();

      return ResponseEntity.ok(bio);
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

  }

  /**
   * Retrieves a users connections
   * <p>
   * Checks if the requester is the user.
   * 
   * @param targetId       ID of the user to retrieve connections for
   * @param authentication
   * @return List of {@link ConnectionResponseDTO}
   */
  @GetMapping("/{targetId}/connections")
  public ResponseEntity<List<ConnectionResponseDTO>> getConnections(@PathVariable Long targetId,
      Authentication authentication) {
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    Long userId = userDetails.getId();

    if (userId == targetId) {
      User user = userService.getUser(userId);
      List<Connection> connections = connectionService.getUserConnections(user);
      List<ConnectionResponseDTO> connectionResponse = new ArrayList<>();
      for (Connection connection : connections) {
        Set<UserResponseDTO> users = connection.getUsers()
            .stream()
            .map(u -> new UserResponseDTO(
                u.getId(),
                u.getEmail(),
                u.getNumber()))
            .collect(Collectors.toSet());
        connectionResponse.add(ConnectionResponseDTO.builder().id(connection.getId()).users(users).build());
      }

      return ResponseEntity.ok(connectionResponse);
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

  }

  // @GetMapping("/settings/{userId}")
  // public ResponseEntity<SettingsResponseDTO> getSettings(@PathVariable Long
  // userId) {

  // SettingsResponseDTO settings = userService.getSettings(userId);

  // return ResponseEntity.ok(settings);
  // }

  // @GetMapping("/attributes/{userId}")
  // public ResponseEntity<AttributesResponseDTO> getAttributes(@PathVariable Long
  // userId) {
  // return ResponseEntity.ok(userService.getAttributes(userId));
  // }

  // @GetMapping("/preferences/{userId}")
  // public ResponseEntity<PreferencesResponseDTO> getPreferences(@PathVariable
  // Long userId) {
  // return ResponseEntity.ok(userService.getPreferences(userId));
  // }

  @PatchMapping("/verify/{userId}")
  public ResponseEntity<?> verifyAccount(@PathVariable Long userId, @RequestParam int verificationCode) {

    userService.verifyAccount(userId, verificationCode);

    return ResponseEntity.ok("Account verified successfully.");
  }

  // @PatchMapping("/settings/{userId}")
  // public ResponseEntity<?> updateSettings(@PathVariable Long userId, @Validated
  // @RequestBody SettingsRequestDTO request) {

  // return ResponseEntity.ok("Settings updated successfully");
  // }

  @PatchMapping("/complete-registration")
  public ResponseEntity<?> setParameters(@Validated @RequestBody UserParametersRequestDTO parameters) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    Long userId = userDetails.getId();

    userService.setUserParameters(userId, parameters);

    return ResponseEntity.ok("Account set-up was successful");
  }

  /*
   * https://stackoverflow.com/questions/49127791/extract-currently-logged-in-user
   * -information-from-jwt-token-using-spring-securit
   * Endpoint: GET /settings/setup
   *
   * Purpose:
   * This endpoint retrieves user parameters for the currently authenticated user.
   * It uses the `Authentication` object to identify the user and fetch their data
   * from the database.
   *
   * How It Works:
   * - The `Authentication` object is used to get the authenticated user's
   * details.
   * - The `UserDetailsImpl` class provides access to the user's ID (`userId`).
   * - The `userService.getParameters(userId)` method fetches and returns the
   * user's parameters.
   *
   * CORS Context:
   * - This endpoint requires the `Authorization` header in the request, which
   * triggers a preflight (OPTIONS) request.
   * - The `CorsFilter` ensures that the backend responds to this preflight
   * request with the necessary CORS headers,
   * allowing the actual GET request to succeed.
   *
   * Example Request:
   * GET /api/user/settings/setup
   * Headers:
   * Authorization: Bearer <JWT_TOKEN>
   */
  @GetMapping("/profile")
  public ResponseEntity<SettingsResponseDTO> getParameters(Authentication authentication) {
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    Long userId = userDetails.getId();
    UserParametersResponseDTO parameters = userService.getParameters(userId);

    SettingsResponseDTO settings = SettingsResponseDTO.builder()
        .email(parameters.email())
        .number(parameters.number())
        .firstName(parameters.first_name())
        .lastName(parameters.last_name())
        .alias(parameters.alias())
        .genderSelf(parameters.gender_self())
        .birthDate(parameters.birth_date())
        .city(parameters.city())
        .longitude(parameters.longitude())
        .latitude(parameters.latitude())
        .genderOther(parameters.gender_other())
        .ageMin(parameters.age_min())
        .ageMax(parameters.age_max())
        .distance(parameters.distance())
        .probabilityTolerance(parameters.probability_tolerance())
        .build();

    return ResponseEntity.ok(settings);
  }

  @PutMapping("/settings/account")
  @Validated
  public ResponseEntity<?> updateAccount(Authentication authentication,
      @Validated @RequestBody AccountSettingsRequestDTO settings) {
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    Long userId = userDetails.getId();

    userService.updateAccountSettings(userId, settings);

    return ResponseEntity.noContent().build();
  }

  @PutMapping("/settings/profile")
  @Validated
  public ResponseEntity<?> updateProfile(Authentication authentication,
      @Validated @RequestBody ProfileSettingsRequestDTO settings) {
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    Long userId = userDetails.getId();

    userService.updateProfileSettings(userId, settings);

    return ResponseEntity.noContent().build();
  }

  @PutMapping("/settings/attributes")
  @Validated
  public ResponseEntity<?> updateAttributes(Authentication authentication,
      @Validated @RequestBody AttributesSettingsRequestDTO settings) {
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    Long userId = userDetails.getId();

    userService.updateAttributesSettings(userId, settings);

    return ResponseEntity.noContent().build();
  }

  @PutMapping("/settings/preferences")
  @Validated
  public ResponseEntity<?> updatePreferences(Authentication authentication,
      @Validated @RequestBody PreferencesSettingsRequestDTO settings) {
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    Long userId = userDetails.getId();

    userService.updatePreferencesSettings(userId, settings);

    return ResponseEntity.noContent().build();
  }
}
