package com.matchme.srv.controller;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.matchme.srv.dto.request.LoginRequestDTO;
import com.matchme.srv.dto.request.SignupRequestDTO;
import com.matchme.srv.dto.response.JwtResponseDTO;
import com.matchme.srv.dto.response.MessageResponseDTO;
import com.matchme.srv.repository.RoleRepository;
import com.matchme.srv.repository.UserRepository;
import com.matchme.srv.security.jwt.JwtUtils;
import com.matchme.srv.security.services.UserDetailsImpl;
import com.matchme.srv.service.UserService;

// public Authcontroller(UserService userService) - constructor?


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UserRepository userRepository;

  @Autowired
  UserService userService;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;

  @GetMapping("/login")
  public String loginPage() {
    return "login";
  }

  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequestDTO loginRequest) {

    Authentication authentication = authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUtils.generateJwtToken(authentication);

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    String role = userDetails.getAuthorities().stream().findFirst().map(GrantedAuthority::getAuthority).orElse("");

    return ResponseEntity
        .ok(new JwtResponseDTO(jwt, userDetails.getId(), userDetails.getEmail(), role));
  }

  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequestDTO signUpRequest) {

    String email = userService.createUser(signUpRequest);

    // if (userRepository.existsByEmail(signUpRequest.getEmail())) {
    //   return ResponseEntity.badRequest().body(new MessageResponseDTO("Error: email is already taken!"));
    // }

    // Create new user's account
    // User user = new User();
    // UserAuth userAuth = new UserAuth();
    // user.setUserAuth(userAuth);

    // user.setEmail(signUpRequest.getEmail());
    // user.setNumber(signUpRequest.getNumber());
    // userAuth.setPassword(encoder.encode(signUpRequest.getPassword()));
    
    // Create userprofile since it's a one-to-one relationship and we can't make a new user without it
    // UserProfile userProfile = new UserProfile();
    // user.setProfile(userProfile);
    // userProfile.setUser(user); might be required, works for now without...  
    
    // Role userRole = roleRepository.findByName(Role.UserRole.ROLE_USER)
    //     .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
    // user.setRole(userRole);
    
    // userRepository.save(user);

    return ResponseEntity.ok(new MessageResponseDTO("User registered successfully! Account verification e-mail was sent to " + email));
  }
}