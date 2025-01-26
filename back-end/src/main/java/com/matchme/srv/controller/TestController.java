package com.matchme.srv.controller;

import com.matchme.srv.dto.request.SignupRequestDTO;
import com.matchme.srv.dto.request.UserParametersRequestDTO;
import com.matchme.srv.dto.response.ChatMessageResponseDTO;
import com.matchme.srv.dto.response.ConnectionResponseDTO;
import com.matchme.srv.model.user.User;
import com.matchme.srv.service.ChatService;
import com.matchme.srv.service.ConnectionService;
import com.matchme.srv.service.user.UserCreationService;
import com.matchme.srv.service.user.UserQueryService;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test")
public class TestController {

  private final UserCreationService creationService;
  private final UserQueryService queryService;
  private final ConnectionService connectionService;
  private final ChatService chatService;

  @GetMapping("/all")
  public String allAccess() {
    return "Public Content.";
  }

  @GetMapping("/user")
  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public String userAccess() {
    return "User Content.";
  }

  @GetMapping("/mod")
  @PreAuthorize("hasRole('MODERATOR')")
  public String moderatorAccess() {
    return "Moderator Board.";
  }

  @GetMapping("/admin")
  @PreAuthorize("hasRole('ADMIN')")
  public String adminAccess() {
    return "Admin Board.";
  }

  @PostMapping("/users")
  public ResponseEntity<Void> bulkCreateUsers(@Valid @RequestBody List<SignupRequestDTO> users) {
    int batchSize = 50;

    for (int i = 0; i < users.size(); i += batchSize) {
      int endIndex = Math.min(i + batchSize, users.size());
      List<SignupRequestDTO> batch = users.subList(i, endIndex);

      for (SignupRequestDTO user : batch) {
        creationService.createUser(user);
      }
    }
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PostMapping("/users/finish")
  public ResponseEntity<Void> bulkSetUserParameters(
      @Valid @RequestBody UserParametersWithEmailsDTO request) {
    List<UserParametersRequestDTO> users = request.parameters();
    List<String> emails = request.emails();

    for (int i = 0; i < users.size(); i++) {
      User existingUser = queryService.getUserByEmail(emails.get(i));
      creationService.setUserParameters(existingUser.getId(), users.get(i));
    }
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PostMapping("/connections")
  public ResponseEntity<List<ConnectionResponseDTO>> bulkCreateConnections(
      @Valid @RequestBody List<UserPair> request) {
    List<ConnectionResponseDTO> createdConnections = new ArrayList<>();
    int batchSize = 100;

    for (int i = 0; i < request.size(); i += batchSize) {
      int endIndex = Math.min(i + batchSize, request.size());
      List<UserPair> batch = request.subList(i, endIndex);

      for (UserPair pair : batch) {
        User user1 = queryService.getUserByEmail(pair.email1());
        User user2 = queryService.getUserByEmail(pair.email2());
        createdConnections.add(connectionService.createConnection(user1, user2));
      }
    }
    return ResponseEntity.status(HttpStatus.CREATED).body(createdConnections);
  }

  @PostMapping("/messages")
  public ResponseEntity<List<ChatMessageResponseDTO>> bulkCreateMessages(
      @Valid @RequestBody List<MessagesSendRequestDTOWithSender> request) {
    List<ChatMessageResponseDTO> createdChatMessages = new ArrayList<>();
    int batchSize = 100;

    for (int i = 0; i < request.size(); i += batchSize) {
      int endIndex = Math.min(i + batchSize, request.size());
      List<MessagesSendRequestDTOWithSender> batch = request.subList(i, endIndex);

      for (MessagesSendRequestDTOWithSender message : batch) {
        User sender = queryService.getUserByEmail(message.senderEmail());
        createdChatMessages.add(
            chatService.saveMessage(
                message.connectionId(), sender.getId(), message.content(), message.timestamp()));
      }
    }
    return ResponseEntity.status(HttpStatus.CREATED).body(createdChatMessages);
  }

  @DeleteMapping("/users")
  public ResponseEntity<Void> bulkDeleteUsersByEmail(@RequestBody List<String> emails) {
    for (int i = 0; i < emails.size(); i++) {
      creationService.removeUserByEmail(emails.get(i));
    }
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  public record UserParametersWithEmailsDTO(
      List<UserParametersRequestDTO> parameters, List<String> emails) {}

  public record UserPair(String email1, String email2) {}

  public record MessagesSendRequestDTOWithSender(
      Long connectionId, String content, String senderEmail, Instant timestamp) {}
}
