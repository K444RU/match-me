package com.matchme.srv.controller;

import com.matchme.srv.dto.graphql.ChatMessageWrapper;
import com.matchme.srv.dto.graphql.ChatPreviewWrapper;
import com.matchme.srv.dto.request.MarkReadRequestDTO;
import com.matchme.srv.dto.request.MessagesSendRequestDTO;
import com.matchme.srv.dto.response.ChatMessageResponseDTO;
import com.matchme.srv.dto.response.ChatPreviewResponseDTO;
import com.matchme.srv.model.user.User;
import com.matchme.srv.publisher.ChatPublisher;
import com.matchme.srv.security.jwt.SecurityUtils;
import com.matchme.srv.service.ChatService;
import com.matchme.srv.service.user.UserQueryService;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatGraphqlController {
  private final UserQueryService queryService;
  private final ChatService chatService;
  private final SecurityUtils securityUtils;
  private final ChatPublisher chatPublisher;
  private final OnlineStatusGraphqlController onlineStatusGraphqlController;

  @QueryMapping
  public List<ChatPreviewWrapper> chatPreviews(Authentication authentication) {
    Long userId = securityUtils.getCurrentUserId(authentication);

    log.info("GraphQL: Fetching chat previews for user ID: {}", userId);

    List<ChatPreviewResponseDTO> previews = chatService.getChatPreviews(userId);
    List<ChatPreviewWrapper> wrappers =
        previews.stream().map(ChatPreviewWrapper::new).collect(Collectors.toList());

    log.debug("GraphQL: Retrieved {} chat previews for user ID: {}", wrappers.size(), userId);

    return wrappers;
  }

  @QueryMapping
  public List<ChatMessageWrapper> chatMessages(
      @Argument String connectionId,
      @Argument String before,
      @Argument Integer limit,
      Authentication authentication) {

    Long userId = securityUtils.getCurrentUserId(authentication);
    Long connectionIdLong = Long.parseLong(connectionId);

    log.info(
        "GraphQL: Fetching chat messages for connection ID: {} by user ID: {}",
        connectionIdLong,
        userId);

    // Default limit if not provided
    int messageLimit = limit != null ? limit : 10;

    Pageable pageable = PageRequest.of(0, messageLimit);

    Page<ChatMessageResponseDTO> messagesPage =
        chatService.getChatMessages(connectionIdLong, userId, pageable);
    List<ChatMessageWrapper> messages =
        messagesPage.getContent().stream()
            .map(ChatMessageWrapper::new)
            .collect(Collectors.toList());

    log.debug(
        "GraphQL: Retrieved {} chat messages for connection ID: {}",
        messages.size(),
        connectionIdLong);

    return messages;
  }

  @MutationMapping
  public ChatMessageResponseDTO sendMessage(
      @Argument MessagesSendRequestDTO input, Authentication authentication) {
    Long senderId = securityUtils.getCurrentUserId(authentication);
    User sender = queryService.getUser(senderId);

    log.info(
        "GraphQL: Received chat message from user ID: {} for connection ID: {}",
        senderId,
        input.getConnectionId());

    Long otherUserId = chatService.getOtherUserIdInConnection(input.getConnectionId(), senderId);
    boolean isOtherUserOnline = onlineStatusGraphqlController.isUserOnline(otherUserId);

    ChatMessageResponseDTO savedMessage =
        chatService.saveMessage(
            input.getConnectionId(),
            sender.getId(),
            input.getContent(),
            Instant.now(),
            isOtherUserOnline);

    chatPublisher.publishMessage(senderId, savedMessage);
    chatPublisher.publishMessage(otherUserId, savedMessage);

    List<ChatPreviewResponseDTO> senderPreviews = chatService.getChatPreviews(senderId);
    List<ChatPreviewResponseDTO> otherUserPreviews = chatService.getChatPreviews(otherUserId);

    chatPublisher.publishPreviews(senderId, senderPreviews);
    chatPublisher.publishPreviews(otherUserId, otherUserPreviews);

    log.debug(
        "GraphQL: Published message ID: {} and previews to users {} and {}",
        savedMessage.getMessageId(),
        senderId,
        otherUserId);

    return savedMessage;
  }

  @MutationMapping
  public ChatPreviewResponseDTO markMessagesAsRead(
      @Argument MarkReadRequestDTO input, Authentication authentication) {

    Long userId = securityUtils.getCurrentUserId(authentication);
    Long connectionId = input.getConnectionId();

    log.info(
        "GraphQL: Received mark read request for connection ID: {} from user ID: {}",
        connectionId,
        userId);

    try {
      ChatPreviewResponseDTO updatedPreview = chatService.markMessagesAsRead(connectionId, userId);

      chatPublisher.publishPreviews(userId, List.of(updatedPreview));

      log.debug(
          "GraphQL: Published updated preview for connection {} to user {}", connectionId, userId);

      return updatedPreview;
    } catch (IllegalArgumentException e) {
      log.error(
          "GraphQL: Error marking messages as read for connection {}: {}",
          connectionId,
          e.getMessage());
      return null;
    } catch (Exception e) {
      log.error(
          "GraphQL: Unexpected error marking messages as read for connection {}: {}",
          connectionId,
          e.getMessage(),
          e);
      return null;
    }
  }


}


