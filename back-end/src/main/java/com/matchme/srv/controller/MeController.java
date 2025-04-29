package com.matchme.srv.controller;

import com.matchme.srv.dto.response.BiographicalResponseDTO;
import com.matchme.srv.dto.response.CurrentUserResponseDTO;
import com.matchme.srv.dto.response.ProfileResponseDTO;
import com.matchme.srv.dto.response.SettingsResponseDTO;
import com.matchme.srv.security.jwt.SecurityUtils;
import com.matchme.srv.service.user.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class MeController {

    private final UserQueryService queryService;
    private final UserController userController;
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
        return userController.getUser(currentUserId, authentication);
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
        ProfileResponseDTO response = queryService.getUserProfileDTO(currentUserId, currentUserId);
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
                queryService.getBiographicalResponseDTO(currentUserId, currentUserId);
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
                queryService.getSettingsResponseDTO(currentUserId, currentUserId);
        return ResponseEntity.ok(response);
    }
}
