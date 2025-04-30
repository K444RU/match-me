package com.matchme.srv.controller;

import com.matchme.srv.dto.graphql.ConnectionUpdateEvent;
import com.matchme.srv.publisher.ConnectionPublisher;
import com.matchme.srv.security.jwt.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

@Controller
@RequiredArgsConstructor
public class SubscriptionController {
  private final ConnectionPublisher connectionPublisher;
  private final SecurityUtils securityUtils;

  @SubscriptionMapping
  public Publisher<ConnectionUpdateEvent> connectionUpdates(Authentication authentication) {
    if (authentication == null) {
      return Flux.empty();
    }

    Long currentUserId = securityUtils.getCurrentUserId(authentication);
    return connectionPublisher.getPublisher(currentUserId);
  }
}
