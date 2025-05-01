package com.matchme.srv.controller;

import com.matchme.srv.dto.graphql.ConnectionUpdateEvent;
import com.matchme.srv.dto.graphql.OnlineStatusEvent;
import com.matchme.srv.dto.graphql.TypingStatusEvent;
import com.matchme.srv.publisher.ConnectionPublisher;
import com.matchme.srv.publisher.OnlineStatusPublisher;
import com.matchme.srv.publisher.TypingStatusPublisher;
import com.matchme.srv.security.jwt.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.graphql.data.method.annotation.Argument;
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
  private final TypingStatusPublisher typingStatusPublisher;
  private final OnlineStatusPublisher onlineStatusPublisher;
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

  @SubscriptionMapping
  public Publisher<TypingStatusEvent> typingStatusUpdates(Authentication authentication, @Argument Long connectionId) {
    if (authentication == null) {
      log.warn("No authentication provided for typing status updates subscription");
      return Flux.empty();
    }

    log.debug("Typing status updates subscription received for user with name: {}", authentication.getName());
    Long currentUserId = securityUtils.getCurrentUserId(authentication);
    log.debug("Typing status updates subscription received for user with id: {}", currentUserId);
    return typingStatusPublisher.getPublisher(currentUserId, connectionId);
  }

  @SubscriptionMapping
  public Publisher<OnlineStatusEvent> onlineStatusUpdates(Authentication authentication, @Argument Long connectionId) {
    if (authentication == null) {
      log.warn("No authentication provided for online status updates subscription");
      return Flux.empty();
    }

    log.debug("Online status updates subscription received for user with name: {}", authentication.getName());
    Long currentUserId = securityUtils.getCurrentUserId(authentication);
    log.debug("Online status updates subscription received for user with id: {}", currentUserId);
    return onlineStatusPublisher.getPublisher(currentUserId, connectionId);
  }
}
