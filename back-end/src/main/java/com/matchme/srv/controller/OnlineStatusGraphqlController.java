package com.matchme.srv.controller;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import com.matchme.srv.dto.graphql.PongDTO;
import com.matchme.srv.dto.response.ChatPreviewResponseDTO;
import com.matchme.srv.dto.graphql.OnlineStatusEvent;
import com.matchme.srv.security.jwt.SecurityUtils;
import com.matchme.srv.service.ChatService;
import com.matchme.srv.publisher.ChatPublisher;
import com.matchme.srv.publisher.OnlineStatusPublisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class OnlineStatusGraphqlController {
    private final SecurityUtils securityUtils;
    private final ChatService chatService;
    private final ChatPublisher chatPublisher;
    private final OnlineStatusPublisher onlineStatusPublisher;

    private final Set<Long> onlineUsers = Collections.synchronizedSet(new HashSet<>());

    @QueryMapping
    public PongDTO ping(Authentication authentication) {
      Long userId = securityUtils.getCurrentUserId(authentication);
      log.debug("GraphQL: Received ping from user ID: {}", userId);
  
      // Send initial online status upon ping, as client might subscribe after initial load
      sendInitialOnlineStatus(userId);
  
      return new PongDTO(Instant.now().toString(), "ok", userId);
    }
  
    @MutationMapping
    public boolean setOnlineStatus(@Argument boolean isOnline, Authentication authentication) {
      Long userId = securityUtils.getCurrentUserId(authentication);
      log.info("GraphQL: Setting user {} online status to: {}", userId, isOnline);
  
      boolean changed = false;
      if (isOnline) {
        changed = onlineUsers.add(userId);
        if (changed) {
          // Trigger marking messages as received only when coming online
          try {
            // This might need adjustment based on how chatService expects it
            chatService.markAllMessagesAsReceived(userId);
            log.info("GraphQL: Triggered markAllMessagesAsReceived for user {}", userId);
          } catch (Exception e) {
            log.error(
                "GraphQL: Error marking messages as received for user {}: {}",
                userId,
                e.getMessage(),
                e);
          }
        }
      } else {
        changed = onlineUsers.remove(userId);
      }
  
      if (changed) {
        broadcastOnlineStatusUpdate(userId, isOnline);
      } else {
        log.debug("GraphQL: User {} online status already set to {}", userId, isOnline);
      }
      return true;
    }
  
    private void broadcastOnlineStatusUpdate(Long userId, boolean isOnline) {
      List<Long> connectionIds = chatService.getUserConnections(userId);
      log.debug(
          "GraphQL: Broadcasting online status change for user {} (online={}) to {} connections",
          userId,
          isOnline,
          connectionIds.size());
  
      for (Long connectionId : connectionIds) {
        try {
          Long otherUserId = chatService.getOtherUserIdInConnection(connectionId, userId);
  
          // Create status update object for the other user
          OnlineStatusEvent statusUpdate = new OnlineStatusEvent();
          statusUpdate.setConnectionId(connectionId); // Context for the receiver
          statusUpdate.setUserId(userId); // The user whose status changed
          statusUpdate.setIsOnline(isOnline);
  
          // Publish the direct online status update to the *other* user
          onlineStatusPublisher.publishStatus(otherUserId, statusUpdate);
  
          // Fetch all previews for the other user to find the relevant one
          List<ChatPreviewResponseDTO> otherUserPreviews = chatService.getChatPreviews(otherUserId);
  
          // Find the specific preview related to this connectionId and update it
          boolean previewUpdated = false;
          for (ChatPreviewResponseDTO preview : otherUserPreviews) {
            // Assuming connectionId uniquely identifies the chat between userId and otherUserId
            if (preview.getConnectionId().equals(connectionId)) {
              // Check if the other user in this preview is indeed the one whose status changed
              if (preview.getConnectedUserId().equals(userId)) {
                preview.setOnline(isOnline); // Update the online status in the preview
                previewUpdated = true;
                break; // Found and updated the relevant preview
              } else {
                // This case should ideally not happen if connectionId maps correctly,
                // but good to log if it does.
                log.warn(
                    "Preview connectionId {} matched, but otherUserId {} did not match updated user"
                        + " {}",
                    connectionId,
                    preview.getConnectedUserId(),
                    userId);
              }
            }
          }
  
          // If we found and updated the relevant preview, publish the entire updated list
          if (previewUpdated) {
            chatPublisher.publishPreviews(otherUserId, otherUserPreviews);
            log.debug(
                "Published updated preview list to user {} due to status change of user {}",
                otherUserId,
                userId);
          } else {
            log.warn(
                "Could not find preview for connection {} in user {}'s previews to update online"
                    + " status.",
                connectionId,
                otherUserId);
          }
  
        } catch (Exception e) {
          log.error(
              "GraphQL: Error broadcasting online status for user {} in connection {}: {}",
              userId,
              connectionId,
              e.getMessage(),
              e);
        }
      }
    }
  
    private void sendInitialOnlineStatus(Long userId) {
      List<Long> connectionIds = chatService.getUserConnections(userId);
      List<OnlineStatusEvent> onlineStatusesToSend = new java.util.ArrayList<>();
  
      log.debug(
          "GraphQL: Preparing initial online status for user {} across {} connections",
          userId,
          connectionIds.size());
      for (Long connectionId : connectionIds) {
        try {
          Long otherUserId = chatService.getOtherUserIdInConnection(connectionId, userId);
          boolean isOtherUserOnline = isUserOnline(otherUserId);
  
          OnlineStatusEvent statusUpdate = new OnlineStatusEvent();
          statusUpdate.setConnectionId(connectionId);
          statusUpdate.setUserId(otherUserId); // Status of the *other* user
          statusUpdate.setIsOnline(isOtherUserOnline);
          onlineStatusesToSend.add(statusUpdate);
        } catch (Exception e) {
          log.error(
              "GraphQL: Error getting other user for connection {} while sending initial status to"
                  + " user {}",
              connectionId,
              userId,
              e);
        }
      }
  
      // Publish the list of statuses directly to the requesting user
      // This requires a way to publish a list, or publish one by one.
      // Let's assume publishing one by one for simplicity with current publisher structure.
      onlineStatusesToSend.forEach(status -> onlineStatusPublisher.publishStatus(userId, status));
  
      log.debug(
          "GraphQL: Sent initial online status updates to userId: {}, count: {}",
          userId,
          onlineStatusesToSend.size());
    }
  
    private boolean isUserOnline(Long userId) {
      return onlineUsers.contains(userId);
    }
}
