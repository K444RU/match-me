package com.matchme.srv.controller;

import com.matchme.srv.model.connection.Connection;
import com.matchme.srv.model.connection.ConnectionProvider;
import com.matchme.srv.model.connection.ConnectionUpdateMessage;
import com.matchme.srv.model.user.User;
import com.matchme.srv.repository.UserRepository;
import com.matchme.srv.security.jwt.SecurityUtils;
import com.matchme.srv.service.ConnectionService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ConnectionWebSocketController {

    private static final Logger log = LoggerFactory.getLogger(ConnectionWebSocketController.class);

    private final ConnectionService connectionService;
    private final SecurityUtils securityUtils;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;

    @MessageMapping("/connection.sendRequest")
    public void sendConnectionRequest(@Payload Long targetUserId, Authentication authentication) {
        log.info("Received connection request for user: {}", targetUserId);
        Long senderId = securityUtils.getCurrentUserId(authentication);
        log.info("Sender ID: {}", senderId);

        Long connectionId = connectionService.sendConnectionRequest(senderId, targetUserId);
        log.info("Generated connection ID: {}", connectionId);

        String targetEmail = findUserEmail(targetUserId);
        String senderEmail = findUserEmail(senderId);
        log.info("Resolved target email: {}", targetEmail);
        log.info("Resolved sender email: {}", senderEmail);

        ConnectionUpdateMessage newRequestMessage = new ConnectionUpdateMessage("NEW_REQUEST", new ConnectionProvider(connectionId, senderId));
        log.info("Sending NEW_REQUEST to {} with payload: {}", targetEmail, newRequestMessage);
        messagingTemplate.convertAndSendToUser(
                targetEmail,
                "/queue/connectionUpdates",
                newRequestMessage
        );

        ConnectionUpdateMessage requestSentMessage = new ConnectionUpdateMessage("REQUEST_SENT", new ConnectionProvider(connectionId, targetUserId));
        log.info("Sending REQUEST_SENT to {} with payload: {}", senderEmail, requestSentMessage);
        messagingTemplate.convertAndSendToUser(
                senderEmail,
                "/queue/connectionUpdates",
                requestSentMessage
        );

        log.info("Sending test message to /topic/test");
        messagingTemplate.convertAndSend("/topic/test", "Test message from server");
    }

    @MessageMapping("/connection.acceptRequest")
    public void acceptConnectionRequest(@Payload Long connectionId, Authentication authentication) {
        log.info("Received accept request for connection ID: {}", connectionId);
        Long acceptorId = securityUtils.getCurrentUserId(authentication);
        log.info("Acceptor ID: {}", acceptorId);

        Connection connection = connectionService.acceptConnectionRequest(connectionId, acceptorId);
        log.info("Connection accepted. Details: {}", connection);

        Long otherUserId = connection.getUsers().stream()
                .map(User::getId)
                .filter(id -> !id.equals(acceptorId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Invalid connection"));
        log.info("Other user ID: {}", otherUserId);

        String acceptorEmail = findUserEmail(acceptorId);
        String otherEmail = findUserEmail(otherUserId);
        log.info("Acceptor email: {}", acceptorEmail);
        log.info("Other user email: {}", otherEmail);

        ConnectionUpdateMessage acceptedMessageForAcceptor = new ConnectionUpdateMessage("REQUEST_ACCEPTED", new ConnectionProvider(connectionId, otherUserId));
        log.info("Sending REQUEST_ACCEPTED to {} with payload: {}", acceptorEmail, acceptedMessageForAcceptor);
        messagingTemplate.convertAndSendToUser(
                acceptorEmail,
                "/queue/connectionUpdates",
                acceptedMessageForAcceptor
        );

        ConnectionUpdateMessage acceptedMessageForOther = new ConnectionUpdateMessage("REQUEST_ACCEPTED", new ConnectionProvider(connectionId, acceptorId));
        log.info("Sending REQUEST_ACCEPTED to {} with payload: {}", otherEmail, acceptedMessageForOther);
        messagingTemplate.convertAndSendToUser(
                otherEmail,
                "/queue/connectionUpdates",
                acceptedMessageForOther
        );
    }

    @MessageMapping("/connection.rejectRequest")
    public void rejectConnectionRequest(@Payload Long connectionId, Authentication authentication) {
        log.info("Received reject request for connection ID: {}", connectionId);
        Long rejectorId = securityUtils.getCurrentUserId(authentication);
        log.info("Rejector ID: {}", rejectorId);

        Connection connection = connectionService.rejectConnectionRequest(connectionId, rejectorId);
        log.info("Connection rejected. Details: {}", connection);

        Long otherUserId = connection.getUsers().stream()
                .map(User::getId)
                .filter(id -> !id.equals(rejectorId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Invalid connection"));
        log.info("Other user ID: {}", otherUserId);

        String rejectorEmail = findUserEmail(rejectorId);
        String otherEmail = findUserEmail(otherUserId);
        log.info("Rejector email: {}", rejectorEmail);
        log.info("Other user email: {}", otherEmail);

        ConnectionUpdateMessage rejectedMessageForRejector = new ConnectionUpdateMessage("REQUEST_REJECTED", new ConnectionProvider(connectionId, otherUserId));
        log.info("Sending REQUEST_REJECTED to {} with payload: {}", rejectorEmail, rejectedMessageForRejector);
        messagingTemplate.convertAndSendToUser(
                rejectorEmail,
                "/queue/connectionUpdates",
                rejectedMessageForRejector
        );

        ConnectionUpdateMessage rejectedMessageForOther = new ConnectionUpdateMessage("REQUEST_REJECTED", new ConnectionProvider(connectionId, rejectorId));
        log.info("Sending REQUEST_REJECTED to {} with payload: {}", otherEmail, rejectedMessageForOther);
        messagingTemplate.convertAndSendToUser(
                otherEmail,
                "/queue/connectionUpdates",
                rejectedMessageForOther
        );
    }

    @MessageMapping("/connection.disconnect")
    public void disconnect(@Payload Long connectionId, Authentication authentication) {
        log.info("Received disconnect request for connection ID: {}", connectionId);
        Long userId = securityUtils.getCurrentUserId(authentication);
        log.info("User ID requesting disconnect: {}", userId);

        Connection connection = connectionService.disconnect(connectionId, userId);
        log.info("Connection disconnected. Details: {}", connection);

        Long otherUserId = connection.getUsers().stream()
                .map(User::getId)
                .filter(id -> !id.equals(userId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Invalid connection"));
        log.info("Other user ID: {}", otherUserId);

        String userEmail = findUserEmail(userId);
        String otherEmail = findUserEmail(otherUserId);
        log.info("User email: {}", userEmail);
        log.info("Other user email: {}", otherEmail);

        ConnectionUpdateMessage disconnectedMessageForUser = new ConnectionUpdateMessage("DISCONNECTED", new ConnectionProvider(connectionId, otherUserId));
        log.info("Sending DISCONNECTED to {} with payload: {}", userEmail, disconnectedMessageForUser);
        messagingTemplate.convertAndSendToUser(
                userEmail,
                "/queue/connectionUpdates",
                disconnectedMessageForUser
        );

        ConnectionUpdateMessage disconnectedMessageForOther = new ConnectionUpdateMessage("DISCONNECTED", new ConnectionProvider(connectionId, userId));
        log.info("Sending DISCONNECTED to {} with payload: {}", otherEmail, disconnectedMessageForOther);
        messagingTemplate.convertAndSendToUser(
                otherEmail,
                "/queue/connectionUpdates",
                disconnectedMessageForOther
        );
    }

    private String findUserEmail(Long userId) {
        String email = userRepository.findById(userId)
                .map(User::getEmail)
                .orElseThrow(() -> new IllegalStateException("User " + userId + " not found"));
        log.info("findUserEmail: Resolved userId {} to email {}", userId, email);
        return email;
    }
}
