package com.matchme.srv.controller;

import com.matchme.srv.model.connection.Connection;
import com.matchme.srv.model.connection.ConnectionProvider;
import com.matchme.srv.model.connection.ConnectionUpdateMessage;
import com.matchme.srv.model.user.User;
import com.matchme.srv.repository.UserRepository;
import com.matchme.srv.security.jwt.SecurityUtils;
import com.matchme.srv.service.ConnectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ConnectionWebSocketController {

    private final ConnectionService connectionService;
    private final SecurityUtils securityUtils;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;


    @MessageMapping("/connection.sendRequest")
    public void sendConnectionRequest(@Payload Long targetUserId, Authentication authentication) {
        System.out.println("Received connection request for user: " + targetUserId);
        Long senderId = securityUtils.getCurrentUserId(authentication);
        Long connectionId = connectionService.sendConnectionRequest(senderId, targetUserId);

        String targetEmail = findUserEmail(targetUserId);
        String senderEmail = findUserEmail(senderId);

        messagingTemplate.convertAndSendToUser(
                targetEmail,
                "/queue/connectionUpdates",
                new ConnectionUpdateMessage("NEW_REQUEST", new ConnectionProvider(connectionId, senderId))
        );

        messagingTemplate.convertAndSendToUser(
                senderEmail,
                "/queue/connectionUpdates",
                new ConnectionUpdateMessage("REQUEST_SENT", new ConnectionProvider(connectionId, targetUserId))
        );
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

        String acceptorEmail = findUserEmail(acceptorId);
        String otherEmail = findUserEmail(otherUserId);

        messagingTemplate.convertAndSendToUser(
                acceptorEmail,
                "/queue/connectionUpdates",
                new ConnectionUpdateMessage("REQUEST_ACCEPTED", new ConnectionProvider(connectionId, otherUserId))
        );

        messagingTemplate.convertAndSendToUser(
                otherEmail,
                "/queue/connectionUpdates",
                new ConnectionUpdateMessage("REQUEST_ACCEPTED", new ConnectionProvider(connectionId, acceptorId))
        );
    }

    @MessageMapping("/connection/rejectRequest")
    public void rejectConnectionRequest(@Payload Long connectionId, Authentication authentication) {
        Long rejectorId = securityUtils.getCurrentUserId(authentication);
        Connection connection = connectionService.rejectConnectionRequest(connectionId, rejectorId);

        Long otherUserId = connection.getUsers().stream()
                .map(User::getId)
                .filter(id -> !id.equals(rejectorId))
                .findFirst()
                .orElseThrow(()-> new IllegalStateException("Invalid connection"));

        String rejectorEmail = findUserEmail(rejectorId);
        String otherEmail = findUserEmail(otherUserId);

        messagingTemplate.convertAndSendToUser(
                rejectorEmail,
                "/queue/connectionUpdates",
                new ConnectionUpdateMessage("REQUEST_REJECTED", new ConnectionProvider(connectionId, otherUserId))
        );

        messagingTemplate.convertAndSendToUser(
                otherEmail,
                "/queue/connectionUpdates",
                new ConnectionUpdateMessage("REQUEST_REJECTED", new ConnectionProvider(connectionId, rejectorId))
        );
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

        String userEmail = findUserEmail(userId);
        String otherEmail = findUserEmail(otherUserId);

        messagingTemplate.convertAndSendToUser(
                userEmail,
                "/queue/connectionUpdates",
                new ConnectionUpdateMessage("DISCONNECTED", new ConnectionProvider(connectionId, otherUserId))
        );

        messagingTemplate.convertAndSendToUser(
                otherEmail,
                "/queue/connectionUpdates",
                new ConnectionUpdateMessage("DISCONNECTED", new ConnectionProvider(connectionId, userId))
        );
    }

    private String findUserEmail(Long userId) {
        return userRepository.findById(userId)
                .map(User::getEmail)
                .orElseThrow(() -> new IllegalStateException("User " + userId + " not found"));
    }
}
