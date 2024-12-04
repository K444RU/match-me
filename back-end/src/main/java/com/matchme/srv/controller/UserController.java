package com.matchme.srv.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.matchme.srv.dto.request.SettingsRequestDTO;
import com.matchme.srv.dto.response.*;
import com.matchme.srv.service.UserService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/user")
public class UserController {

  private UserService userService;

  @GetMapping("/settings/{userId}")
  public ResponseEntity<SettingsResponseDTO> getSettings(@PathVariable Long userId) {

    SettingsResponseDTO settings = userService.getSettings(userId);

    return ResponseEntity.ok(settings);
  }

  @GetMapping("/attributes/{userId}")
  public ResponseEntity<AttributesResponseDTO> getAttributes(@PathVariable Long userId) {
    return ResponseEntity.ok(userService.getAttributes(userId));
  }

  @GetMapping("/preferences/{userId}")
  public ResponseEntity<PreferencesResponseDTO> getPreferences(@PathVariable Long userId) {
    return ResponseEntity.ok(userService.getPreferences(userId));
  }

  @PatchMapping("/verify/{userId}")
  public ResponseEntity<?> verifyAccount(@PathVariable Long userId, @RequestParam int verificationCode) {

    userService.verifyAccount(userId, verificationCode);

    return ResponseEntity.ok("User verified successfully.");
  }

  @PatchMapping("/settings/{userId}")
  public ResponseEntity<?> updateSettings(@PathVariable Long userId, @Validated @RequestBody SettingsRequestDTO request) {

    return ResponseEntity.ok("Settings updated successfully");
  }

}
