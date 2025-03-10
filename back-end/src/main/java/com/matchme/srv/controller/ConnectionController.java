package com.matchme.srv.controller;

import com.matchme.srv.service.ConnectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import com.matchme.srv.dto.response.MatchingRecommendationsDTO;
import com.matchme.srv.security.jwt.SecurityUtils;
import com.matchme.srv.service.MatchingService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/connections")
public class ConnectionController {

    private final ConnectionService connectionService;
    private final MatchingService matchingService;
    private final SecurityUtils securityUtils;

    @GetMapping("/recommendations")
    public ResponseEntity<MatchingRecommendationsDTO> getMatchingRecommendations(Authentication authentication) {
        Long currentUserId = securityUtils.getCurrentUserId(authentication);
        MatchingRecommendationsDTO response = matchingService.getRecommendations(currentUserId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/request/{userId}")
    public ResponseEntity<?> sendConnectionRequest(@PathVariable Long userId, Authentication authentication) {
        Long currentUserId = securityUtils.getCurrentUserId(authentication);
        connectionService.sendConnectionRequest(currentUserId, userId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/request/{requestId}/accept")
    public ResponseEntity<?> acceptConnectionRequest(@PathVariable Long requestId, Authentication authentication) {
        Long currentUserId = securityUtils.getCurrentUserId(authentication);
        connectionService.acceptConnectionRequest(currentUserId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/requests/{requestId}/reject")
    public ResponseEntity<?> rejectConnectionRequest(@PathVariable Long requestId, Authentication authentication) {
        Long currentUserId = securityUtils.getCurrentUserId(authentication);
        connectionService.rejectConnectionRequest(requestId, currentUserId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{connectionId}")
    public ResponseEntity<?> disconnect(@PathVariable Long connectionId, Authentication authentication) {
        Long currentUserId = securityUtils.getCurrentUserId(authentication);
        connectionService.disconnect(connectionId, currentUserId);
        return ResponseEntity.ok().build();
    }
}
