package com.matchme.srv.api.controllers;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.matchme.srv.Role.Role;
import com.matchme.srv.user.User;
import com.matchme.srv.Role.RoleRepository;
import com.matchme.srv.api.dto.request.LoginRequest;
import com.matchme.srv.api.dto.request.SignupRequest;
import com.matchme.srv.api.dto.response.JwtResponse;
import com.matchme.srv.api.dto.response.MessageResponse;
import com.matchme.srv.enums.ERole;
import com.matchme.srv.user.UserRepository;
import com.matchme.srv.user.user_profile.UserProfile;
import com.matchme.srv.security.jwt.JwtUtils;
import com.matchme.srv.security.services.UserDetailsImpl;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UserRepository userRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;

  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

    Authentication authentication = authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUtils.generateJwtToken(authentication);

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    String role = userDetails.getAuthorities().stream().findFirst().map(GrantedAuthority::getAuthority).orElse("");

    return ResponseEntity
        .ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getEmail(), role));
  }

  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      return ResponseEntity.badRequest().body(new MessageResponse("Error: email is already taken!"));
    }

    // Create new user's account
    User user = new User();
    user.setEmail(signUpRequest.getEmail());
    user.setNumber(signUpRequest.getNumber());
    user.setPassword(encoder.encode(signUpRequest.getPassword()));
    
    // Create userprofile since it's a one-to-one relationship and we can't make a new user without it
    UserProfile userProfile = new UserProfile();
    user.setUserProfile(userProfile);
    // userProfile.setUser(user); might be required, works for now without...  
    
    // Always assign ROLE_USER
    Role userRole = roleRepository.findByName(ERole.ROLE_USER)
        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
    user.setRole(userRole);
    
    userRepository.save(user);

    return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
  }
}