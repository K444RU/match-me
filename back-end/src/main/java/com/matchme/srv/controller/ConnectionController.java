package com.matchme.srv.controller;

import com.matchme.srv.dto.response.ConnectionsDTO;
import com.matchme.srv.service.ConnectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    @GetMapping("")
    public ResponseEntity<ConnectionsDTO> getConnections(Authentication authentication) {
        Long currentUserId = securityUtils.getCurrentUserId(authentication);
        ConnectionsDTO connections = connectionService.getConnections(currentUserId);
        return ResponseEntity.ok(connections);
    }

    @PostMapping("/requests/{userId}")
    public ResponseEntity<Void> sendConnectionRequest(@PathVariable Long userId, Authentication authentication) {
        Long currentUserId = securityUtils.getCurrentUserId(authentication);
        connectionService.sendConnectionRequest(currentUserId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/requests/{requestId}/accept")
    public ResponseEntity<Void> acceptConnectionRequest(@PathVariable Long requestId, Authentication authentication) {
        Long currentUserId = securityUtils.getCurrentUserId(authentication);
        connectionService.acceptConnectionRequest(requestId, currentUserId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/requests/{requestId}/reject")
    public ResponseEntity<Void> rejectConnectionRequest(@PathVariable Long requestId, Authentication authentication) {
        Long currentUserId = securityUtils.getCurrentUserId(authentication);
        connectionService.rejectConnectionRequest(requestId, currentUserId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{connectionId}")
    public ResponseEntity<Void> disconnect(@PathVariable Long connectionId, Authentication authentication) {
        Long currentUserId = securityUtils.getCurrentUserId(authentication);
        connectionService.disconnect(connectionId, currentUserId);
        return ResponseEntity.noContent().build();
    }
}
