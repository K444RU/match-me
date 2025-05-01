package com.matchme.srv.controller;

import com.matchme.srv.dto.graphql.ConnectionUpdateEvent;
import com.matchme.srv.dto.graphql.OnlineStatusEvent;
import com.matchme.srv.dto.graphql.TypingStatusEvent;
import com.matchme.srv.dto.response.ChatMessageResponseDTO;
import com.matchme.srv.dto.response.ChatPreviewResponseDTO;
import com.matchme.srv.dto.response.MessageStatusUpdateDTO;
import com.matchme.srv.publisher.ChatPublisher;
import com.matchme.srv.publisher.ConnectionPublisher;
import com.matchme.srv.publisher.OnlineStatusPublisher;
import com.matchme.srv.publisher.TypingStatusPublisher;
import com.matchme.srv.security.jwt.SecurityUtils;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

@Slf4j
@Controller
@RequiredArgsConstructor
public class SubscriptionController {
  private final ConnectionPublisher connectionPublisher;
  private final TypingStatusPublisher typingStatusPublisher;
  private final OnlineStatusPublisher onlineStatusPublisher;
  private final ChatPublisher chatPublisher;
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
  public Publisher<TypingStatusEvent> typingStatusUpdates(Authentication authentication) {
    if (authentication == null) {
      log.warn("No authentication provided for typing status updates subscription");
      return Flux.empty();
    }

    log.debug("Typing status updates subscription received for user with name: {}", authentication.getName());
    Long currentUserId = securityUtils.getCurrentUserId(authentication);
    log.debug("Typing status updates subscription received for user with id: {}", currentUserId);
    return typingStatusPublisher.getPublisher(currentUserId);
  }

  @SubscriptionMapping
  public Publisher<OnlineStatusEvent> onlineStatusUpdates(Authentication authentication) {
    if (authentication == null) {
      log.warn("No authentication provided for online status updates subscription");
      return Flux.empty();
    }

    log.debug("Online status updates subscription received for user with name: {}", authentication.getName());
    Long currentUserId = securityUtils.getCurrentUserId(authentication);
    log.debug("Online status updates subscription received for user with id: {}", currentUserId);
    return onlineStatusPublisher.getPublisher(currentUserId);
  }

  @SubscriptionMapping
  public Publisher<List<ChatPreviewResponseDTO>> chatPreviews(Authentication authentication) {
    if (authentication == null) {
      log.warn("No authentication provided for previews subscription");
      return Flux.empty();
    }

    log.debug("Previews subscription received for user with name: {}", authentication.getName());
    Long currentUserId = securityUtils.getCurrentUserId(authentication);
    log.debug("Previews subscription received for user with id: {}", currentUserId);
    return chatPublisher.getPreviewPublisher(currentUserId);
  }

  @SubscriptionMapping
  public Publisher<ChatMessageResponseDTO> messages(Authentication authentication) {
    if (authentication == null) {
      log.warn("No authentication provided for messages subscription");
      return Flux.empty();
    }

    log.debug("Messages subscription received for user with name: {}", authentication.getName());
    Long currentUserId = securityUtils.getCurrentUserId(authentication);
    log.debug("Messages subscription received for user with id: {}", currentUserId);

    typingStatusPublisher.resetSinkForUser(currentUserId);
    return chatPublisher.getMessagePublisher(currentUserId);
  }

  @SubscriptionMapping
  public Publisher<MessageStatusUpdateDTO> messageStatus(Authentication authentication) {
    if (authentication == null) {
      log.warn("No authentication provided for message status subscription");
      return Flux.empty();
    }

    log.debug("Message status subscription received for user with name: {}", authentication.getName());
    Long currentUserId = securityUtils.getCurrentUserId(authentication);
    log.debug("Message status subscription received for user with id: {}", currentUserId);
    return chatPublisher.getMessageStatusPublisher(currentUserId);
  }
}
