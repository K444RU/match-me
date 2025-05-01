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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class SubscriptionController {
  private final ConnectionPublisher connectionPublisher;
  private final SecurityUtils securityUtils;

  @SubscriptionMapping
  public Publisher<ConnectionUpdateEvent> connectionUpdates(Authentication authentication) {
    if (authentication == null) {
      log.warn("No authentication provided for connection updates subscription");
      return Flux.empty();
    }

    log.debug("Connection updates subscription received for user with name: {}", authentication.getName());
    Long currentUserId = securityUtils.getCurrentUserId(authentication);
    log.debug("Connection updates subscription received for user with id: {}", currentUserId);
    return connectionPublisher.getPublisher(currentUserId);
  }
}
