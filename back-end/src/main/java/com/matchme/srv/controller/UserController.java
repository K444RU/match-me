package com.matchme.srv.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.matchme.srv.dto.request.UserParametersRequestDTO;
import com.matchme.srv.security.services.UserDetailsImpl;
import com.matchme.srv.service.UserService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/user")
public class UserController {

  @Autowired
  private UserService userService;

  // @GetMapping("/settings/{userId}")
  // public ResponseEntity<SettingsResponseDTO> getSettings(@PathVariable Long userId) {

  //   SettingsResponseDTO settings = userService.getSettings(userId);

  //   return ResponseEntity.ok(settings);
  // }

  // @GetMapping("/attributes/{userId}")
  // public ResponseEntity<AttributesResponseDTO> getAttributes(@PathVariable Long userId) {
  //   return ResponseEntity.ok(userService.getAttributes(userId));
  // }

  // @GetMapping("/preferences/{userId}")
  // public ResponseEntity<PreferencesResponseDTO> getPreferences(@PathVariable Long userId) {
  //   return ResponseEntity.ok(userService.getPreferences(userId));
  // }

  @PatchMapping("/verify/{userId}")
  public ResponseEntity<?> verifyAccount(@PathVariable Long userId, @RequestParam int verificationCode) {

    userService.verifyAccount(userId, verificationCode);

    return ResponseEntity.ok("Account verified successfully.");
  }

  // @PatchMapping("/settings/{userId}")
  // public ResponseEntity<?> updateSettings(@PathVariable Long userId, @Validated @RequestBody SettingsRequestDTO request) {

  //   return ResponseEntity.ok("Settings updated successfully");
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
   * https://stackoverflow.com/questions/49127791/extract-currently-logged-in-user-information-from-jwt-token-using-spring-securit
   * Endpoint: GET /settings/setup
   *
   * Purpose:
   * This endpoint retrieves user parameters for the currently authenticated user.
   * It uses the `Authentication` object to identify the user and fetch their data from the database.
   *
   * How It Works:
   * - The `Authentication` object is used to get the authenticated user's details.
   * - The `UserDetailsImpl` class provides access to the user's ID (`userId`).
   * - The `userService.getParameters(userId)` method fetches and returns the user's parameters.
   *
   * CORS Context:
   * - This endpoint requires the `Authorization` header in the request, which triggers a preflight (OPTIONS) request.
   * - The `CorsFilter` ensures that the backend responds to this preflight request with the necessary CORS headers,
   *   allowing the actual GET request to succeed.
   *
   * Example Request:
   * GET /api/user/settings/setup
   * Headers:
   *   Authorization: Bearer <JWT_TOKEN>
   */
  @GetMapping("/settings/setup")
  public ResponseEntity<?> getParameters(Authentication authentication) {
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    Long userId = userDetails.getId();

    return ResponseEntity.ok(userService.getParameters(userId));
  }

}
