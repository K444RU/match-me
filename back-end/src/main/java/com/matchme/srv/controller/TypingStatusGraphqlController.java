package com.matchme.srv.controller;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import com.matchme.srv.dto.graphql.TypingStatusEvent;
import com.matchme.srv.publisher.TypingStatusPublisher;
import com.matchme.srv.security.jwt.SecurityUtils;
import com.matchme.srv.service.ChatService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class TypingStatusGraphqlController {
    private final TypingStatusPublisher typingStatusPublisher;
    private final SecurityUtils securityUtils;
    private final ChatService chatService;

    @MutationMapping
    public boolean typingStatus(
        @Argument TypingStatusEvent input, Authentication authentication) {
      Long senderId = securityUtils.getCurrentUserId(authentication);
      Long connectionId = input.getConnectionId();
      boolean isTyping = input.getIsTyping();
  
      // Ensure the sender in the request matches the authenticated user
      if (!senderId.equals(input.getSenderId())) {
        log.warn(
            "GraphQL: typingStatus senderId mismatch. Auth: {}, Input: {}",
            senderId,
            input.getSenderId());
        input.setSenderId(senderId);
      }
  
      log.debug(
          "GraphQL: Received typing status: user {} typing={} in connection {}",
          senderId,
          isTyping,
          connectionId);
  
      try {
        Long otherUserId = chatService.getOtherUserIdInConnection(connectionId, senderId);
  
        typingStatusPublisher.publishStatus(otherUserId, input);
  
        return true;
      } catch (Exception e) {
        log.error(
            "GraphQL: Error processing typing status for connection {}: {}",
            connectionId,
            e.getMessage(),
            e);
        return false;
      }
    }
}
