package com.matchme.srv.controller;

import static com.matchme.srv.TestDataFactory.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.matchme.srv.dto.request.MarkReadRequestDTO;
import com.matchme.srv.dto.request.MessagesSendRequestDTO;
import com.matchme.srv.dto.request.TypingStatusRequestDTO;
import com.matchme.srv.dto.response.ChatMessageResponseDTO;
import com.matchme.srv.dto.response.ChatPreviewResponseDTO;
import com.matchme.srv.model.user.User;
import com.matchme.srv.security.jwt.SecurityUtils;
import com.matchme.srv.service.ChatService;
import com.matchme.srv.service.user.UserQueryService;
import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class ChatWebSocketControllerTests {

  @InjectMocks private ChatWebSocketController chatWebSocketController;

  @Mock private ChatService chatService;

  @Mock private UserQueryService userQueryService;

  @Mock private SimpMessagingTemplate messagingTemplate;

  @Mock private SecurityUtils securityUtils;

  @Captor ArgumentCaptor<ChatMessageResponseDTO> messageCaptor;
  @Captor ArgumentCaptor<List<ChatPreviewResponseDTO>> previewsCaptor;
  @Captor ArgumentCaptor<ChatPreviewResponseDTO> previewCaptor;

  private User sender;
  private Authentication authentication;
  private static final Instant TEST_TIMESTAMP = Instant.now().truncatedTo(ChronoUnit.SECONDS);

  @BeforeEach
  void setUp() {
    sender = createUser(DEFAULT_USER_ID, DEFAULT_EMAIL);
    authentication = new UsernamePasswordAuthenticationToken(sender, null);

    when(securityUtils.getCurrentUserId(authentication)).thenReturn(sender.getId());
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
    }

    @Test
    @DisplayName("Should save message and broadcast to both users")
    void sendMessage_ValidRequest_BroadcastsToBothUsers() {
      // Arrange
      when(userQueryService.getUser(DEFAULT_USER_ID)).thenReturn(sender);
      when(chatService.getOtherUserIdInConnection(123L, DEFAULT_USER_ID))
          .thenReturn(DEFAULT_TARGET_USER_ID);

      // Act
      chatWebSocketController.sendMessage(validRequest, authentication);

      // Assert
      assertAll(
          () ->
              verify(chatService)
                  .saveMessage(
                      eq(validRequest.getConnectionId()),
                      eq(sender.getId()),
                      eq(validRequest.getContent()),
                      any(Instant.class)),
          () ->
              verify(messagingTemplate)
                  .convertAndSendToUser(
                      DEFAULT_USER_ID.toString(), "/queue/messages", mockResponse),
          () ->
              verify(messagingTemplate)
                  .convertAndSendToUser(
                      DEFAULT_TARGET_USER_ID.toString(), "/queue/messages", mockResponse),
          () ->
              verify(chatService)
                  .getOtherUserIdInConnection(
                      eq(validRequest.getConnectionId()), eq(sender.getId())));
    }

    @Test
    @DisplayName("Should propagate connection not found error")
    void sendMessage_InvalidConnection_HandlesError() {
      // Arrange
      when(chatService.getOtherUserIdInConnection(any(), any()))
          .thenThrow(new EntityNotFoundException("Connection not found"));
      when(userQueryService.getUser(sender.getId())).thenReturn(sender);

      // Act & Assert
      assertThatThrownBy(() -> chatWebSocketController.sendMessage(validRequest, authentication))
          .as("Should propagate connection not found error")
          .isInstanceOf(EntityNotFoundException.class)
          .hasMessageContaining("Connection not found");

      verify(chatService).saveMessage(any(), any(), any(), any());
      verify(messagingTemplate, times(0)).convertAndSendToUser(any(), any(), any());
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
      validRequest.setSenderId(DEFAULT_USER_ID);
      validRequest.setIsTyping(true);
    }

    @Test
    @DisplayName("Should send typing status to other user")
    void typingStatus_ValidRequest_SendsToOtherUser() {
      // Arrange
      when(chatService.getOtherUserIdInConnection(123L, DEFAULT_USER_ID))
          .thenReturn(DEFAULT_TARGET_USER_ID);

      // Act
      chatWebSocketController.typingStatus(validRequest, authentication);

      // Assert
      verify(messagingTemplate)
          .convertAndSendToUser(DEFAULT_TARGET_USER_ID.toString(), "/queue/typing", validRequest);
    }

    @Test
    @DisplayName("Should ignore request if sender ID mismatches")
    void typingStatus_MismatchedSender_IgnoresRequest() {
      // Arrange
      validRequest.setSenderId(INVALID_USER_ID);

      // Act
      chatWebSocketController.typingStatus(validRequest, authentication);

      // Assert
      verify(messagingTemplate, times(0)).convertAndSendToUser(any(), any(), any());
      verify(chatService, times(0)).getOtherUserIdInConnection(any(), any());
      verify(securityUtils, times(1)).getCurrentUserId(any());
    }
  }

  @Nested
  @DisplayName("markMessagesAsRead Tests")
  class MarkMessagesAsReadTests {
    private MarkReadRequestDTO validRequest;
    private ChatPreviewResponseDTO mockPreviewResponse;

    @BeforeEach
    void setUp() {
      validRequest = new MarkReadRequestDTO();
      validRequest.setConnectionId(DEFAULT_CONNECTION_ID);

      mockPreviewResponse = new ChatPreviewResponseDTO();
      mockPreviewResponse.setConnectionId(DEFAULT_CONNECTION_ID);
      mockPreviewResponse.setConnectedUserId(DEFAULT_TARGET_USER_ID);
      mockPreviewResponse.setConnectedUserAlias(DEFAULT_TARGET_ALIAS);
      mockPreviewResponse.setLastMessageContent("Last message content");
      mockPreviewResponse.setLastMessageTimestamp(TEST_TIMESTAMP);
      mockPreviewResponse.setUnreadMessageCount(0);

      when(chatService.markMessagesAsRead(DEFAULT_CONNECTION_ID, DEFAULT_USER_ID))
          .thenReturn(mockPreviewResponse);
    }

    @Test
    @DisplayName("Should call service and send updated preview to user")
    void markMessagesAsRead_ValidRequest_CallsServiceAndSendsPreview() {
      // Act
      chatWebSocketController.markMessagesAsRead(validRequest, authentication);

      // Assert Service Call
      verify(chatService).markMessagesAsRead(DEFAULT_CONNECTION_ID, DEFAULT_USER_ID);

      // Assert Messaging Template Call
      verify(messagingTemplate)
          .convertAndSendToUser(
              eq(DEFAULT_USER_ID.toString()), eq("/queue/previews"), previewCaptor.capture());
      assertThat(previewCaptor.getValue()).isEqualTo(mockPreviewResponse);
    }

    @Test
    @DisplayName("Should log error and not send message if service throws IllegalArgumentException")
    void markMessagesAsRead_ServiceThrowsIllegalArgumentException_LogsErrorDoesNotSend() {
      // Arrange
      when(chatService.markMessagesAsRead(DEFAULT_CONNECTION_ID, DEFAULT_USER_ID))
          .thenThrow(new IllegalArgumentException("Connection not found"));

      // Act
      // No exception should propagate from the controller method itself due to try-catch
      chatWebSocketController.markMessagesAsRead(validRequest, authentication);

      // Assert Service Call (still attempted)
      verify(chatService).markMessagesAsRead(DEFAULT_CONNECTION_ID, DEFAULT_USER_ID);

      // Assert Messaging Template Call (should NOT have been called)
      verifyNoInteractions(messagingTemplate);

      // Note: We can't easily verify logging with Mockito alone.
      // Integration tests or specific logging frameworks might be needed for that.
    }

     @Test
     @DisplayName("Should log error and not send message if service throws other Exception")
     void markMessagesAsRead_ServiceThrowsRuntimeException_LogsErrorDoesNotSend() {
        // Arrange
        when(chatService.markMessagesAsRead(DEFAULT_CONNECTION_ID, DEFAULT_USER_ID))
            .thenThrow(new RuntimeException("Unexpected database error"));

        // Act
        chatWebSocketController.markMessagesAsRead(validRequest, authentication);

        // Assert Service Call (still attempted)
        verify(chatService).markMessagesAsRead(DEFAULT_CONNECTION_ID, DEFAULT_USER_ID);

        // Assert Messaging Template Call (should NOT have been called)
        verifyNoInteractions(messagingTemplate);
     }
  }
}