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

  @GetMapping("/settings/setup/{userId}")
  public ResponseEntity<?> getParameters(@PathVariable Long userId) {

    return ResponseEntity.ok(userService.getParameters(userId));
  }

}
