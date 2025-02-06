package com.matchme.srv.controller;

import java.util.List;

import com.matchme.srv.dto.request.settings.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.matchme.srv.dto.request.UserParametersRequestDTO;
import com.matchme.srv.dto.response.BiographicalResponseDTO;
import com.matchme.srv.dto.response.ConnectionResponseDTO;
import com.matchme.srv.dto.response.CurrentUserResponseDTO;
import com.matchme.srv.dto.response.ProfileResponseDTO;
import com.matchme.srv.security.jwt.SecurityUtils;
import com.matchme.srv.service.ConnectionService;
import com.matchme.srv.service.user.UserCreationService;
import com.matchme.srv.service.user.UserProfileService;
import com.matchme.srv.service.user.UserQueryService;
import com.matchme.srv.service.user.UserSettingsService;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserCreationService creationService;
    private final UserSettingsService settingsService;
    private final UserProfileService profileService;
    private final UserQueryService queryService;
    private final ConnectionService connectionService;
    private final SecurityUtils securityUtils;

    /**
     * Retrieves a users basic information by ID
     * <p>
     * Checks if the requester is connected or is the user.
     *
     * @param targetId ID of the user to retrieve
     * @param authentication
     * @return ID, email, first_name, last_name, alias and roles
     * @see CurrentUserResponseDTO
     */
    @GetMapping("/{targetId}")
    public ResponseEntity<CurrentUserResponseDTO> getUser(@PathVariable Long targetId,
            Authentication authentication) {
        Long currentUserId = securityUtils.getCurrentUserId(authentication);
        CurrentUserResponseDTO response = queryService.getCurrentUserDTO(currentUserId, targetId);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves a users profile
     * <p>
     * Checks if the requester is connected or is the user.
     *
     * @param targetId ID of the user to retrieve profile for
     * @param authentication
     * @return first_name, last_name and city
     * @see ProfileResponseDTO
     */
    @GetMapping("/{targetId}/profile")
    public ResponseEntity<ProfileResponseDTO> getProfile(@PathVariable Long targetId,
            Authentication authentication) {
        Long currentUserId = securityUtils.getCurrentUserId(authentication);
        ProfileResponseDTO response = queryService.getUserProfileDTO(currentUserId, targetId);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves a users biographical data
     * <p>
     * Checks if the requester is connected or is the user
     *
     * @param targetId ID of the user to retrieve biographical data for
     * @param authentication
     * @return User age, preferences and attributes
     * @see BiographicalResponseDTO
     */
    @GetMapping("/{targetId}/bio")
    public ResponseEntity<BiographicalResponseDTO> getBio(@PathVariable Long targetId,
            Authentication authentication) {
        Long currentUserId = securityUtils.getCurrentUserId(authentication);
        BiographicalResponseDTO response =
            queryService.getBiographicalResponseDTO(currentUserId, targetId);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves a users connections
     * <p>
     * Checks if the requester is the user.
     *
     * @param targetId ID of the user to retrieve connections for
     * @param authentication
     * @return List of {@link ConnectionResponseDTO}
     */
    @GetMapping("/{targetId}/connections")
    public ResponseEntity<List<ConnectionResponseDTO>> getConnections(@PathVariable Long targetId,
            Authentication authentication) {
        Long currentUserId = securityUtils.getCurrentUserId(authentication);
        List<ConnectionResponseDTO> response =
                connectionService.getConnectionResponseDTO(currentUserId, targetId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/verify/{userId}")
    public ResponseEntity<Void> verifyAccount(@PathVariable Long userId,
            @RequestParam int verificationCode) {
            creationService.verifyAccount(userId, verificationCode);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/complete-registration")
    public ResponseEntity<Void> setParameters(
            @Validated @RequestBody UserParametersRequestDTO parameters,
            Authentication authentication) {
        Long currentUserId = securityUtils.getCurrentUserId(authentication);
        creationService.setUserParameters(currentUserId, parameters);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/settings/account")
    @Validated
    public ResponseEntity<Void> updateAccount(Authentication authentication,
            @Validated @RequestBody AccountSettingsRequestDTO settings) {
        Long currentUserId = securityUtils.getCurrentUserId(authentication);
        settingsService.updateAccountSettings(currentUserId, settings);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/settings/profile")
    @Validated
    public ResponseEntity<Void> updateProfile(Authentication authentication,
            @Validated @RequestBody ProfileSettingsRequestDTO settings) {
        Long currentUserId = securityUtils.getCurrentUserId(authentication);
        settingsService.updateProfileSettings(currentUserId, settings);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/settings/attributes")
    @Validated
    public ResponseEntity<Void> updateAttributes(Authentication authentication,
            @Validated @RequestBody AttributesSettingsRequestDTO settings) {
        Long currentUserId = securityUtils.getCurrentUserId(authentication);
        settingsService.updateAttributesSettings(currentUserId, settings);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/settings/preferences")
    @Validated
    public ResponseEntity<Void> updatePreferences(Authentication authentication,
            @Validated @RequestBody PreferencesSettingsRequestDTO settings) {
        Long currentUserId = securityUtils.getCurrentUserId(authentication);
        settingsService.updatePreferencesSettings(currentUserId, settings);
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint for uploading a user's profile picture.
     *
     * Delegates validation and business logic to the service layer. Handles specific exceptions
     * like invalid input, entity not found, and general errors.
     *
     * @param request DTO containing base64 image string (optional).
     * @param authentication Current authenticated user.
     * @return Success or error message wrapped in a ResponseEntity.
     */
    @PostMapping("/profile-picture")
    public ResponseEntity<Void> uploadProfilePicture(
            @RequestBody(required = false) ProfilePictureSettingsRequestDTO request,
            Authentication authentication) {
        Long currentUserId = securityUtils.getCurrentUserId(authentication);
        profileService.saveProfilePicture(currentUserId, request);
        return ResponseEntity.ok().build();
    }

}
