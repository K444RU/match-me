package com.matchme.srv.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.matchme.srv.dto.response.CurrentUserResponseDTO;
import com.matchme.srv.model.user.User;
import com.matchme.srv.model.user.profile.UserProfile;
import com.matchme.srv.security.services.UserDetailsImpl;
import com.matchme.srv.service.UserService;

@RestController
@RequestMapping("/api")
public class MeController {

    @Autowired
    UserService userService;

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

    // @GetMapping("/me/profile")
    // @GetMapping("/me/bio")
    // @GetMapping("/recommendations")
    // @GetMapping("/connections")
}
