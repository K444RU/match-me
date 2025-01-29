package com.matchme.srv.controller;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.matchme.srv.dto.request.MessagesSendRequestDTO;
import com.matchme.srv.dto.request.TypingStatusRequestDTO;
import com.matchme.srv.dto.response.ChatMessageResponseDTO;
import com.matchme.srv.model.user.User;
import com.matchme.srv.security.jwt.SecurityUtils;
import com.matchme.srv.service.ChatService;
import com.matchme.srv.service.user.UserQueryService;
import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@WebMvcTest(controllers = ChatWebSocketController.class)
public class ChatWebSocketControllerTests {

  @Autowired private ChatWebSocketController chatWebSocketController;

  @MockitoBean private ChatService chatService;

  @MockitoBean private UserQueryService userQueryService;

  @MockitoBean private SimpMessagingTemplate messagingTemplate;

  @MockitoBean private SecurityUtils securityUtils;

  private User sender;
  private User receiver;
  private Authentication authentication;

  @BeforeEach
  void setUp() {
    sender = new User();
    sender.setId(1L);
    receiver = new User();
    receiver.setId(2L);
    authentication = new UsernamePasswordAuthenticationToken(sender, null);

    when(securityUtils.getCurrentUserId(authentication)).thenReturn(sender.getId());
    when(userQueryService.getUser(sender.getId())).thenReturn(sender);
  }

  @Nested
  @DisplayName("sendMessage Tests")
  class SendMessageTests {
    private MessagesSendRequestDTO validRequest;
    private ChatMessageResponseDTO mockResponse;

    @BeforeEach
    void setUp() {
      validRequest = new MessagesSendRequestDTO();
      validRequest.setConnectionId(123L);
      validRequest.setContent("Test message");

      mockResponse =
          ChatMessageResponseDTO.builder()
              .messageId(456L)
              .content("Test message")
              .createdAt(Instant.now())
              .build();

      when(chatService.saveMessage(any(), any(), any(), any())).thenReturn(mockResponse);
      when(chatService.getOtherUserIdInConnection(123L, 1L)).thenReturn(2L);
    }

    @Test
    @DisplayName("sendMessage_ValidRequest_BroadcastsToBothUsers")
    void sendMessage_ValidRequest_BroadcastsToBothUsers() {
      // Act
      chatWebSocketController.sendMessage(validRequest, authentication);

      // Assert
      verify(chatService)
          .saveMessage(
              eq(validRequest.getConnectionId()),
              eq(sender.getId()),
              eq(validRequest.getContent()),
              any(Instant.class));

      verify(messagingTemplate)
          .convertAndSendToUser(sender.getId().toString(), "/queue/messages", mockResponse);

      verify(messagingTemplate).convertAndSendToUser("2", "/queue/messages", mockResponse);
    }

    @Test
    @DisplayName("sendMessage_InvalidConnection_HandlesError")
    void sendMessage_InvalidConnection_HandlesError() {
      // Arrange
      when(chatService.getOtherUserIdInConnection(any(), any()))
          .thenThrow(new EntityNotFoundException("Connection not found"));

      // Act & Assert
      assertThatThrownBy(() -> chatWebSocketController.sendMessage(validRequest, authentication))
          .as("Should propagate connection not found error")
          .isInstanceOf(EntityNotFoundException.class)
          .hasMessageContaining("Connection not found");
    }
  }

  @Nested
  @DisplayName("typingStatus Tests")
  class TypingStatusTests {
    private TypingStatusRequestDTO validRequest;

    @BeforeEach
    void setUp() {
      validRequest = new TypingStatusRequestDTO();
      validRequest.setConnectionId(123L);
      validRequest.setSenderId(1L);
      validRequest.setTyping(true);

      when(chatService.getOtherUserIdInConnection(123L, 1L)).thenReturn(2L);
    }

    @Test
    @DisplayName("typingStatus_ValidRequest_SendsToOtherUser")
    void typingStatus_ValidRequest_SendsToOtherUser() {
      // Act
      chatWebSocketController.typingStatus(validRequest, authentication);

      // Assert
      verify(messagingTemplate).convertAndSendToUser("2", "/queue/typing", validRequest);
    }

    @Test
    @DisplayName("typingStatus_MismatchedSender_IgnoresRequest")
    void typingStatus_MismatchedSender_IgnoresRequest() {
      // Arrange
      validRequest.setSenderId(999L);

      // Act
      chatWebSocketController.typingStatus(validRequest, authentication);

      // Assert
      verify(messagingTemplate, times(0)).convertAndSendToUser(any(), any(), any());
    }
  }
}
