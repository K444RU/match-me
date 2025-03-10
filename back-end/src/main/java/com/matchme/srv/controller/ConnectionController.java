package com.matchme.srv.controller;

import com.matchme.srv.service.ConnectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


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

}
