package com.matchme.srv.controller;

import com.matchme.srv.model.connection.Connection;
import com.matchme.srv.model.connection.ConnectionProvider;
import com.matchme.srv.model.connection.ConnectionUpdateMessage;
import com.matchme.srv.model.user.User;
import com.matchme.srv.security.jwt.SecurityUtils;
import com.matchme.srv.service.ConnectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ConnectionWebSocketController {

    private final ConnectionService connectionService;
    private final SecurityUtils securityUtils;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/connection.sendRequest")
    public void sendConnectionRequest(@Payload Long targetUserId, Authentication authentication) {
        log.info("Received connection request for user: " + targetUserId);
        Long senderId = securityUtils.getCurrentUserId(authentication);
        Long connectionId = connectionService.sendConnectionRequest(senderId, targetUserId);

        messagingTemplate.convertAndSendToUser(
                targetUserId.toString(),
                "/queue/connectionUpdates",
                new ConnectionUpdateMessage("NEW_REQUEST", new ConnectionProvider(connectionId, senderId))
        );
        log.info("NEW_REQUEST: Sent connection request to user: " + targetUserId);

        messagingTemplate.convertAndSendToUser(
                senderId.toString(),
                "/queue/connectionUpdates",
                new ConnectionUpdateMessage("REQUEST_SENT", new ConnectionProvider(connectionId, targetUserId))
        );
        log.info("REQUEST_SENT: Sent connection request to user: " + targetUserId);
    }

    @MessageMapping("/connection.acceptRequest")
    public void acceptConnectionRequest(@Payload Long connectionId, Authentication authentication) {
        Long acceptorId = securityUtils.getCurrentUserId(authentication);
        Connection connection = connectionService.acceptConnectionRequest(connectionId, acceptorId);

        Long otherUserId = connection.getUsers().stream()
                .map(User::getId)
                .filter(id -> !id.equals(acceptorId))
                .findFirst()
                .orElseThrow(()-> new IllegalStateException("Invalid connection"));

        messagingTemplate.convertAndSendToUser(
                acceptorId.toString(),
                "/queue/connectionUpdates",
                new ConnectionUpdateMessage("REQUEST_ACCEPTED", new ConnectionProvider(connectionId, otherUserId))
        );
        log.info("REQUEST_ACCEPTED: Sent connection request to user: " + otherUserId);

        messagingTemplate.convertAndSendToUser(
                otherUserId.toString(),
                "/queue/connectionUpdates",
                new ConnectionUpdateMessage("REQUEST_ACCEPTED", new ConnectionProvider(connectionId, acceptorId))
        );
        log.info("REQUEST_ACCEPTED: Sent connection request to user: " + acceptorId);
    }

    @MessageMapping("/connection.rejectRequest")
    public void rejectConnectionRequest(@Payload Long connectionId, Authentication authentication) {
        Long rejectorId = securityUtils.getCurrentUserId(authentication);
        Connection connection = connectionService.rejectConnectionRequest(connectionId, rejectorId);

        Long otherUserId = connection.getUsers().stream()
                .map(User::getId)
                .filter(id -> !id.equals(rejectorId))
                .findFirst()
                .orElseThrow(()-> new IllegalStateException("Invalid connection"));

        messagingTemplate.convertAndSendToUser(
                rejectorId.toString(),
                "/queue/connectionUpdates",
                new ConnectionUpdateMessage("REQUEST_REJECTED", new ConnectionProvider(connectionId, otherUserId))
        );
        log.info("REQUEST_REJECTED: Sent connection request to user: " + otherUserId);

        messagingTemplate.convertAndSendToUser(
                otherUserId.toString(),
                "/queue/connectionUpdates",
                new ConnectionUpdateMessage("REQUEST_REJECTED", new ConnectionProvider(connectionId, rejectorId))
        );
        log.info("REQUEST_REJECTED: Sent connection request to user: " + rejectorId);
    }

    @MessageMapping("/connection.disconnect")
    public void disconnect(@Payload Long connectionId, Authentication authentication) {
        Long userId = securityUtils.getCurrentUserId(authentication);
        Connection connection = connectionService.disconnect(connectionId, userId);

        Long otherUserId = connection.getUsers().stream()
                .map(User::getId)
                .filter(id -> !id.equals(userId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Invalid connection"));

        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/connectionUpdates",
                new ConnectionUpdateMessage("DISCONNECTED", new ConnectionProvider(connectionId, otherUserId))
        );
        log.info("DISCONNECTED: Sent connection request to user: " + otherUserId);

        messagingTemplate.convertAndSendToUser(
                otherUserId.toString(),
                "/queue/connectionUpdates",
                new ConnectionUpdateMessage("DISCONNECTED", new ConnectionProvider(connectionId, userId))
        );
        log.info("DISCONNECTED: Sent connection request to user: " + userId);
    }
}
