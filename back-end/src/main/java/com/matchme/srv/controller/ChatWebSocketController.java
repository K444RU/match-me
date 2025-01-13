package com.matchme.srv.controller;

import com.matchme.srv.dto.request.MessagesSendRequestDTO;
import com.matchme.srv.dto.request.TypingStatusRequestDTO;
import com.matchme.srv.dto.response.ChatMessageResponseDTO;
import com.matchme.srv.model.user.User;
import com.matchme.srv.security.jwt.SecurityUtils;
import com.matchme.srv.service.ChatService;
import com.matchme.srv.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.time.Instant;

/**
 * This controller handles the real-time WebSocket-based chat functionalities:
 * - Sending messages
 * - Typing indicators
 * - Online/offline status (can be handled by presence events)
 * <p>
 * It relies on:
 * - The WebSocketConfig that sets up the endpoints and message broker.
 * - The ChatService and potentially specialized services (e.g. ChatMessageSendingService)
 * that handle logic related to sending messages, updating unread counts, etc.
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketController {

    private final UserService userService;
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;
    private final SecurityUtils securityUtils;

    /**
     * Send a chat message from one user to another.
     * send the message payload to /app/chat.sendMessage.
     * <p>
     * The controller:
     * 1. Identifies the authenticated user (sender).
     * 2. Saves the message via chatService.
     * 3. Broadcasts the saved message to both the sender and the receiver.
     */
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload MessagesSendRequestDTO messageDTO,
                            Authentication authentication) {
        Long senderId = securityUtils.getCurrentUserId(authentication);
        User sender = userService.getUser(senderId);

        log.info("Received chat message from user ID: {} for connection ID: {}",
                        senderId, messageDTO.getConnectionId());

        ChatMessageResponseDTO savedMessage = chatService.saveMessage(
                messageDTO.getConnectionId(),
                sender.getId(),
                messageDTO.getContent(),
                Instant.now()
        );

        Long otherUserId = chatService.getOtherUserIdInConnection(messageDTO.getConnectionId(), senderId);

        // Broadcast the message to both participants in real-time
        // The "/user/{userId}/queue/messages" destination will deliver the message privately
        messagingTemplate.convertAndSendToUser(
                senderId.toString(),
                "/queue/messages",
                savedMessage
        );

        messagingTemplate.convertAndSendToUser(
                otherUserId.toString(),
                "/queue/messages",
                savedMessage
        );

        log.debug("Broadcasting message ID: {} to users {} and {}",
                savedMessage.getMessageId(), senderId, otherUserId);
    }

    /**
     * Handle typing indicator events.
     * Clients send a message to /app/chat.typing when they start or stop typing.
     * <p>
     * The server sends a typing notification to the other participant. The frontend can show
     * a "User is typing..." message.
     */
    @MessageMapping("/chat.typing")
    public void typingStatus(@Payload TypingStatusRequestDTO typingStatusRequest,
                             Authentication authentication) {
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
                otherUserId.toString(),
                "/queue/typing",
                typingStatusRequest
        );
    }

    /*
    * toDo:
    * for online/offline status -> track WebSocket connections.
    * We can use a @EventListener for SessionConnectEvent and SessionDisconnectEvent
    * to track who is online and broadcast their status changes
    *
    * @EventListener
    * public void handleWebSocketConnectListener(SessionConnectEvent event) {
    *
    * }
    *
    * @EventListener
    * public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
    *
    * }
    *
    * private void trackOnlineStatus(Long userId, boolean isOnline) {
    *
    * }
    */
}