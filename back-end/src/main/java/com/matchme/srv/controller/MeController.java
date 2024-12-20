package com.matchme.srv.controller;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.matchme.srv.dto.response.BiographicalResponseDTO;
import com.matchme.srv.dto.response.ConnectionResponseDTO;
import com.matchme.srv.dto.response.CurrentUserResponseDTO;
import com.matchme.srv.dto.response.GenderTypeDTO;
import com.matchme.srv.dto.response.ProfileResponseDTO;
import com.matchme.srv.dto.response.UserResponseDTO;
import com.matchme.srv.model.connection.Connection;
import com.matchme.srv.model.user.User;
import com.matchme.srv.model.user.profile.UserProfile;
import com.matchme.srv.security.services.UserDetailsImpl;
import com.matchme.srv.service.ConnectionService;
import com.matchme.srv.service.UserService;

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
     * @return ID, email, first_name, last_name, alias and roles
     * @see CurrentUserResponseDTO
     */
    @GetMapping("/me")
    public ResponseEntity<CurrentUserResponseDTO> getCurrentUser(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getId();
        UserProfile userProfile = userService.getUserProfile(userId);
        User user = userService.getUser(userId);

        CurrentUserResponseDTO currentUser = CurrentUserResponseDTO.builder()
                .id(userId)
                .email(user.getEmail())
                .firstName(userProfile != null ? userProfile.getFirst_name() : null)
                .lastName(userProfile != null ? userProfile.getLast_name() : null)
                .alias(userProfile != null ? userProfile.getAlias() : null)
                .role(user.getRoles())
                .build();

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
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getId();
        UserProfile userProfile = userService.getUserProfile(userId);

        ProfileResponseDTO profile = ProfileResponseDTO.builder()
                .first_name(userProfile.getFirst_name())
                .last_name(userProfile.getLast_name())
                .city(userProfile.getCity())
                .build();

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
