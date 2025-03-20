package com.matchme.srv.controller;

import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.matchme.srv.dto.response.MatchingRecommendationsDTO;
import com.matchme.srv.security.jwt.SecurityUtils;
import com.matchme.srv.service.MatchingService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class ConnectionController {

  private final MatchingService matchingService;
  private final SecurityUtils securityUtils;

  @GetMapping("/recommendations")
  public ResponseEntity<MatchingRecommendationsDTO> getMatchingRecommendations(Authentication authentication) {

    Long currentUserId = securityUtils.getCurrentUserId(authentication);

    MatchingRecommendationsDTO response = matchingService.getRecommendations(currentUserId);
    return ResponseEntity.ok(response);
  }

}
