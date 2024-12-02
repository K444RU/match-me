package com.matchme.srv.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.matchme.srv.dto.response.SettingsResponseDTO;
import com.matchme.srv.service.UserService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/user")
public class UserController {
  private UserService userService;


  @GetMapping("/settings")
  public ResponseEntity<SettingsResponseDTO> getSettings(Long userId) {

    SettingsResponseDTO settings = userService.getSettings(userId);

    return ResponseEntity.ok(settings);
  }

  // @PostMapping("/settings")
  // public String updateSettings(Long userId) {

  // }
}
