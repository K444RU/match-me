package com.matchme.srv.controller;

import com.matchme.srv.dto.response.*;
import com.matchme.srv.model.connection.Connection;
import com.matchme.srv.model.user.User;
import com.matchme.srv.model.user.profile.UserProfile;
import com.matchme.srv.security.services.UserDetailsImpl;
import com.matchme.srv.service.ConnectionService;
import com.matchme.srv.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class MeController {

    @Autowired
    UserService userService;

    @Autowired
    ConnectionService connectionService;

    /**
     * Retrieves the current authenticated users basic information.
     * 
     * @return ID, email, first_name, last_name, alias, profile_picture and roles
     * @see CurrentUserResponseDTO
     */
    @GetMapping("/me")
    public ResponseEntity<CurrentUserResponseDTO> getCurrentUser(Authentication authentication) {
        Long userId = ((UserDetailsImpl) authentication.getPrincipal()).getId();
        CurrentUserResponseDTO currentUser = userService.getCurrentUserDTO(userId);
        return ResponseEntity.ok(currentUser);
    }

    /**
     * Retrieves the current authenticated users profile.
     * 
     * @return first_name, last_name and city
     * @see ProfileResponseDTO
     */
    @GetMapping("/me/profile")
    public ResponseEntity<ProfileResponseDTO> getCurrentProfile(Authentication authentication) {
        Long userId = ((UserDetailsImpl) authentication.getPrincipal()).getId();
        ProfileResponseDTO profile = userService.getUserProfileDTO(userId, userId);
        return ResponseEntity.ok(profile);
    }

    /**
     * Retrieves the current authenticated users biographical data.
     * 
     * @return User age, preferences and attributes
     * @see BiographicalResponseDTO
     */
    @GetMapping("/me/bio")
    public ResponseEntity<BiographicalResponseDTO> getCurrentBio(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getId();
        UserProfile userProfile = userService.getUserProfile(userId);

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
  @GetMapping("/me/settings")
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

    /**
     * Retrieves the current authenticated users connections.
     * 
     * @return List of {@link ConnectionResponseDTO}
     */
    @GetMapping("/connections")
    public ResponseEntity<List<ConnectionResponseDTO>> getConnections(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getId();
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
    }

    // TODO: /recommendations
    // @GetMapping("/recommendations")
}
