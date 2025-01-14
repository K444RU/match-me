package com.matchme.srv.controller;

import com.matchme.srv.dto.response.*;
import com.matchme.srv.security.jwt.SecurityUtils;
import com.matchme.srv.service.ConnectionService;
import com.matchme.srv.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class MeController {

    private final UserService userService;
    private final ConnectionService connectionService;
    private final SecurityUtils securityUtils;

    /**
     * Retrieves the current authenticated users basic information.
     * 
     * @return ID, email, first_name, last_name, alias, profile_picture and roles
     * @see CurrentUserResponseDTO
     */
    @GetMapping("/me")
    public ResponseEntity<CurrentUserResponseDTO> getCurrentUser(Authentication authentication) {
        Long currentUserId = securityUtils.getCurrentUserId(authentication);
        CurrentUserResponseDTO response = userService.getCurrentUserDTO(currentUserId);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves the current authenticated users profile.
     * 
     * @return first_name, last_name and city
     * @see ProfileResponseDTO
     */
    @GetMapping("/me/profile")
    public ResponseEntity<ProfileResponseDTO> getCurrentProfile(Authentication authentication) {
        Long currentUserId = securityUtils.getCurrentUserId(authentication);
        ProfileResponseDTO response = userService.getUserProfileDTO(currentUserId, currentUserId);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves the current authenticated users biographical data.
     * 
     * @return User age, preferences and attributes
     * @see BiographicalResponseDTO
     */
    @GetMapping("/me/bio")
    public ResponseEntity<BiographicalResponseDTO> getCurrentBio(Authentication authentication) {
        Long currentUserId = securityUtils.getCurrentUserId(authentication);
        BiographicalResponseDTO response =
                userService.getBiographicalResponseDTO(currentUserId, currentUserId);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves all user parameters for settings editing on front-end
     * 
     * @return user, profile, bio
     * @see SettingsResponseDTO
     */
    @GetMapping("/me/settings")
    public ResponseEntity<SettingsResponseDTO> getParameters(Authentication authentication) {
        Long currentUserId = securityUtils.getCurrentUserId(authentication);
        SettingsResponseDTO response =
                userService.getSettingsResponseDTO(currentUserId, currentUserId);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves the current authenticated users connections.
     * 
     * @return List of {@link ConnectionResponseDTO}
     */
    @GetMapping("/connections")
    public ResponseEntity<List<ConnectionResponseDTO>> getConnections(
            Authentication authentication) {
        Long currentUserId = securityUtils.getCurrentUserId(authentication);
        List<ConnectionResponseDTO> response =
                connectionService.getConnectionResponseDTO(currentUserId, currentUserId);
        return ResponseEntity.ok(response);
    }

    // TODO: /recommendations
    // @GetMapping("/recommendations")
}
