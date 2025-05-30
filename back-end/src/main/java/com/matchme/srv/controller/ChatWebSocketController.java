package com.matchme.srv.controller;

import com.matchme.srv.dto.request.MarkReadRequestDTO;
import com.matchme.srv.dto.request.MessagesSendRequestDTO;
import com.matchme.srv.dto.request.OnlineStatusResponseDTO;
import com.matchme.srv.dto.request.TypingStatusRequestDTO;
import com.matchme.srv.dto.response.ChatMessageResponseDTO;
import com.matchme.srv.dto.response.ChatPreviewResponseDTO;
import com.matchme.srv.model.user.User;
import com.matchme.srv.security.jwt.SecurityUtils;
import com.matchme.srv.service.ChatService;
import com.matchme.srv.service.user.UserQueryService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/**
 * This controller handles the real-time WebSocket-based chat functionalities: - Sending messages -
 * Typing indicators - Online/offline status (can be handled by presence events)
 *
 * <p>It relies on: - The WebSocketConfig that sets up the endpoints and message broker. - The
 * ChatService and potentially specialized services (e.g. ChatMessageSendingService) that handle
 * logic related to sending messages, updating unread counts, etc.
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketController {

  private final UserQueryService queryService;
  private final ChatService chatService;
  private final SimpMessagingTemplate messagingTemplate;
  private final SecurityUtils securityUtils;

  private static final String QUEUE_PREFIX = "/queue";
  private static final String ONLINE_QUEUE = QUEUE_PREFIX + "/online";
  private static final String MESSAGES_QUEUE = QUEUE_PREFIX + "/messages";
  private static final String PREVIEWS_QUEUE = QUEUE_PREFIX + "/previews";
  private static final String TYPING_QUEUE = QUEUE_PREFIX + "/typing";
  private static final String PONG_QUEUE = QUEUE_PREFIX + "/pong";

  private final Set<Long> onlineUsers = Collections.synchronizedSet(new HashSet<>());

  /**
   * Send a chat message from one user to another. send the message payload to
   * /app/chat.sendMessage.
   *
   * <p>The process: 1. Identifies the authenticated user (sender). 2. Saves the message via
   * chatService. 3. Broadcasts the saved message to both the sender and the receiver. 4. Fetches
   * updated chat previews for both participants. 5. Broadcasts the updated chat previews to both
   * users. WebSocket destinations used: - "/user/{userId}/queue/messages": Sends the new message
   * privately to each user. - "/user/{userId}/queue/previews": Sends the updated chat previews
   * privately to each user.
   *
   * <p>This ensures that both users receive real-time updates for new messages and chat previews.
   *
   * @param messageDTO The message payload containing the connection ID and content.
   * @param authentication The authentication object to retrieve the current user's details.
   */
  @MessageMapping("/chat.sendMessage")
  public void sendMessage(
      @Payload MessagesSendRequestDTO messageDTO, Authentication authentication) {
    Long senderId = securityUtils.getCurrentUserId(authentication);
    User sender = queryService.getUser(senderId);

    log.info(
        "Received chat message from user ID: {} for connection ID: {}",
        senderId,
        messageDTO.getConnectionId());

    Long otherUserId =
        chatService.getOtherUserIdInConnection(messageDTO.getConnectionId(), senderId);

    boolean isOtherUserOnline = isUserOnline(otherUserId);
    log.debug("Recipient user ID {} online status: {}", otherUserId, isOtherUserOnline);

    ChatMessageResponseDTO savedMessage =
        chatService.saveMessage(
            messageDTO.getConnectionId(),
            sender.getId(),
            messageDTO.getContent(),
            Instant.now(),
            isOtherUserOnline);

    // Broadcast the message to both participants in real-time
    // The "/user/{userId}/queue/messages" destination will deliver the message privately
    messagingTemplate.convertAndSendToUser(senderId.toString(), MESSAGES_QUEUE, savedMessage);

    messagingTemplate.convertAndSendToUser(otherUserId.toString(), MESSAGES_QUEUE, savedMessage);

    List<ChatPreviewResponseDTO> senderPreviews = chatService.getChatPreviews(senderId);
    List<ChatPreviewResponseDTO> otherUserPreviews = chatService.getChatPreviews(otherUserId);

    messagingTemplate.convertAndSendToUser(senderId.toString(), PREVIEWS_QUEUE, senderPreviews);

    messagingTemplate.convertAndSendToUser(
        otherUserId.toString(), PREVIEWS_QUEUE, otherUserPreviews);

    log.debug(
        "Broadcasting message ID: {} to users {} and {}",
        savedMessage.getMessageId(),
        senderId,
        otherUserId);
  }

  /**
   * Handles requests from clients to mark messages in a specific connection as read. After marking
   * messages, it pushes the updated chat preview for that connection back to the user who initiated
   * the request.
   *
   * <p>Clients send messages to /app/chat.markRead
   *
   * @param markReadRequest DTO containing the connectionId to mark as read.
   * @param authentication The authentication object to identify the current user.
   */
  @MessageMapping("/chat.markRead")
  public void markMessagesAsRead(
      @Payload MarkReadRequestDTO markReadRequest, Authentication authentication) {

    Long userId = securityUtils.getCurrentUserId(authentication);
    Long connectionId = markReadRequest.getConnectionId();

    log.info(
        "Received mark read request for connection ID: {} from user ID: {}", connectionId, userId);

    try {
      ChatPreviewResponseDTO updatedPreview = chatService.markMessagesAsRead(connectionId, userId);

      messagingTemplate.convertAndSendToUser(userId.toString(), PREVIEWS_QUEUE, updatedPreview);

      log.debug("Sent updated preview for connection {} to user {}", connectionId, userId);

    } catch (IllegalArgumentException e) {
      log.error(
          "Error marking messages as read for connection {}: {}", connectionId, e.getMessage());
    } catch (Exception e) {
      log.error(
          "Unexpected error marking messages as read for connection {}: {}",
          connectionId,
          e.getMessage(),
          e);
    }
  }

  /**
   * Handle typing indicator events. Clients send a message to /app/chat.typing when they start or
   * stop typing.
   *
   * <p>The server sends a typing notification to the other participant. The frontend can show a
   * "User is typing..." message.
   */
  @MessageMapping("/chat.typing")
  public void typingStatus(
      @Payload TypingStatusRequestDTO typingStatusRequest, Authentication authentication) {
    Long senderId = securityUtils.getCurrentUserId(authentication);

    // Ensure the sender in the request matches the authenticated user
    if (!senderId.equals(typingStatusRequest.getSenderId())) {
      // If mismatched, ignore or throw an exception
      return;
    }

    Long connectionId = typingStatusRequest.getConnectionId();
    Long otherUserId = chatService.getOtherUserIdInConnection(connectionId, senderId);

    // Broadcast typing status to the other participant
    messagingTemplate.convertAndSendToUser(
        otherUserId.toString(), TYPING_QUEUE, typingStatusRequest);
  }

  /**
   * Handle ping-pong events to verify connectivity. This is used by clients to test subscription
   * connectivity.
   */
  @MessageMapping("/chat.ping")
  public void handlePing(@Payload String payload, Authentication authentication) {
    Long userId = securityUtils.getCurrentUserId(authentication);

    log.debug("Received ping from user ID: {}", userId);

    // Parse the payload to get any extra data
    String responsePayload =
        String.format(
            "{ \"timestamp\": \"%s\", \"status\": \"ok\", \"userId\": %d, \"message\": \"Server"
                + " acknowledged ping\" }",
            Instant.now(), userId);

    // Send a pong back to the sender to confirm subscription works
    log.debug("Sending pong to user {}", userId);
    messagingTemplate.convertAndSendToUser(userId.toString(), PONG_QUEUE, responsePayload);

    // Also send the current online status information after ping
    // This ensures client gets status info even if they missed the initial broadcast
    sendInitialOnlineStatus(userId);
  }

  @EventListener
  public void handleWebSocketConnectListener(SessionConnectEvent event) {
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

    // Extract user
    Authentication auth = (Authentication) accessor.getUser();
    if (auth != null) {
      Long userId = securityUtils.getCurrentUserId(auth);
      setUserOnline(userId, true);

      try {
        chatService.markAllMessagesAsReceived(userId);
      } catch (Exception e) {
        log.error("Error marking messages as received for user {}: {}", userId, e.getMessage(), e);
      }
    } else {
      log.warn("User connected but Authentication was null in SessionConnectEvent.");
    }
  }

  @EventListener
  public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

    // Extract user
    Authentication auth = (Authentication) accessor.getUser();
    if (auth != null) {
      Long userId = securityUtils.getCurrentUserId(auth);
      setUserOnline(userId, false);
    }
  }

  private void trackOnlineStatus(Long userId, boolean isOnline) {
    List<Long> connections = chatService.getUserConnections(userId);

    for (Long connectionId : connections) {
      Long otherUserId = chatService.getOtherUserIdInConnection(connectionId, userId);

      // Create status update object
      OnlineStatusResponseDTO statusUpdate = new OnlineStatusResponseDTO();
      statusUpdate.setConnectionId(connectionId);
      statusUpdate.setUserId(userId);
      statusUpdate.setIsOnline(isOnline);

      // Send to the other user
      messagingTemplate.convertAndSendToUser(otherUserId.toString(), ONLINE_QUEUE, statusUpdate);
    }

    log.debug("Online status update for userId: {}, isOnline: {}", userId, isOnline);
  }

  private void sendInitialOnlineStatus(Long userId) {
    List<Long> connections = chatService.getUserConnections(userId);
    List<OnlineStatusResponseDTO> onlineStatuses = new ArrayList<>();

    for (Long connectionId : connections) {
      Long otherUserId = chatService.getOtherUserIdInConnection(connectionId, userId);
      boolean isOnline = isUserOnline(otherUserId);

      OnlineStatusResponseDTO statusUpdate = new OnlineStatusResponseDTO();
      statusUpdate.setConnectionId(connectionId);
      statusUpdate.setUserId(otherUserId);
      statusUpdate.setIsOnline(isOnline);
      onlineStatuses.add(statusUpdate);
    }

    messagingTemplate.convertAndSendToUser(userId.toString(), ONLINE_QUEUE, onlineStatuses);
    log.debug("Sent initial online status to userId: {}, count: {}", userId, onlineStatuses.size());
  }

  private void setUserOnline(Long userId, boolean isOnline) {
    if (isOnline) {
      onlineUsers.add(userId);
    } else {
      onlineUsers.remove(userId);
    }

    trackOnlineStatus(userId, isOnline);
  }

  private boolean isUserOnline(Long userId) {
    return onlineUsers.contains(userId);
  }
}
