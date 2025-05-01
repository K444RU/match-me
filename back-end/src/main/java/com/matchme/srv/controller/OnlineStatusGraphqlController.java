package com.matchme.srv.controller;

import com.matchme.srv.dto.graphql.OnlineStatusEvent;
import com.matchme.srv.dto.graphql.PongDTO;
import com.matchme.srv.publisher.ChatPublisher;
import com.matchme.srv.publisher.OnlineStatusPublisher;
import com.matchme.srv.security.jwt.SecurityUtils;
import com.matchme.srv.service.ChatService;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

@Controller
@RequiredArgsConstructor
@Slf4j
@EnableScheduling
public class OnlineStatusGraphqlController {
  private final SecurityUtils securityUtils;
  private final ChatService chatService;
  private final OnlineStatusPublisher onlineStatusPublisher;

  private final Set<Long> onlineUsers = Collections.synchronizedSet(new HashSet<>());
  private final Map<Long, Instant> lastPingTimes = new ConcurrentHashMap<>();
  private static final Duration PING_TIMEOUT_DURATION = Duration.ofSeconds(5);

  @QueryMapping
  @Transactional
  public PongDTO ping(Authentication authentication) {
    Long userId = securityUtils.getCurrentUserId(authentication);
    Instant now = Instant.now();

    // Record the ping time
    lastPingTimes.put(userId, now);

    // Check if user wasn't considered online before this ping
    boolean becameOnline =
        onlineUsers.add(userId); // add returns true if the element was not already present

    if (becameOnline) {
      // Broadcast online status as they just came online
      broadcastOnlineStatusUpdate(userId, true);

      // Mark messages as received now that they are confirmed online
      try {
        chatService.markAllMessagesAsReceived(userId);
      } catch (Exception e) {
        log.error(
            "Error calling markAllMessagesAsReceived for user {} after ping: {}",
            userId,
            e.getMessage(),
            e);
        // Decide if this error should affect the response or just be logged
      }
    }

    List<OnlineStatusEvent> initialPeerStatuses = getInitialPeerStatuses(userId);

    return new PongDTO(now.toString(), "ok", userId, initialPeerStatuses);
  }

  private List<OnlineStatusEvent> getInitialPeerStatuses(Long userId) {
    List<OnlineStatusEvent> peerStatuses = new ArrayList<>();
    try {
      List<Long> connectionIds = chatService.getUserConnections(userId);

      for (Long connectionId : connectionIds) {
        try {
          Long otherUserId = chatService.getOtherUserIdInConnection(connectionId, userId);
          boolean isOtherUserOnline = isUserOnline(otherUserId);

          OnlineStatusEvent statusUpdate = new OnlineStatusEvent();
          statusUpdate.setConnectionId(connectionId);
          statusUpdate.setUserId(otherUserId);
          statusUpdate.setIsOnline(isOtherUserOnline);
          peerStatuses.add(statusUpdate);
        } catch (IllegalArgumentException e) {
          log.warn(
              "Could not find other user for connection {} while calculating initial status for user {}: {}",
              connectionId,
              userId,
              e.getMessage());
        } catch (Exception e) {
          log.error(
              "Error calculating peer status for user {} connection {}: {}",
              userId,
              connectionId,
              e.getMessage(),
              e);
        }
      }
    } catch (Exception e) {
      log.error(
          "Error retrieving connections for user {} during initial status calculation: {}",
          userId,
          e.getMessage(),
          e);
    }
    return peerStatuses;
  }

  @Scheduled(fixedRate = 5000)
  public void checkPingTimeouts() {
    Instant cutoffTime = Instant.now().minus(PING_TIMEOUT_DURATION);

    // Find users who haven't pinged since the cutoff time
    List<Long> timedOutUserIds = new ArrayList<>();
    lastPingTimes.forEach(
        (userId, lastPing) -> {
          if (onlineUsers.contains(userId) && lastPing.isBefore(cutoffTime)) {
            timedOutUserIds.add(userId);
          }
          else if (!onlineUsers.contains(userId)
              && lastPing.isBefore(cutoffTime.minusSeconds(60))) {
            lastPingTimes.remove(userId);
          }
        });

    if (!timedOutUserIds.isEmpty()) {
      log.warn("Users timed out due to lack of ping: {}", timedOutUserIds);
      for (Long userId : timedOutUserIds) {
        boolean removed = onlineUsers.remove(userId);
        lastPingTimes.remove(userId);

        if (removed) {
          broadcastOnlineStatusUpdate(userId, false);
        }
      }
    }
  }

  private void broadcastOnlineStatusUpdate(Long userId, boolean isOnline) {
    try {
      // Use ChatService to find connections where this user participates
      List<Long> connectionIds = chatService.getUserConnections(userId);
      log.debug(
          "Broadcasting status update ({} is {}) for {} connections.",
          userId,
          isOnline ? "online" : "offline",
          connectionIds.size());

      for (Long connectionId : connectionIds) {
        try {
          // Find the *other* user in the connection to send the update to
          Long otherUserId = chatService.getOtherUserIdInConnection(connectionId, userId);
          OnlineStatusEvent statusUpdate = new OnlineStatusEvent();
          statusUpdate.setConnectionId(connectionId); // Context for the recipient
          statusUpdate.setUserId(userId); // The user whose status changed
          statusUpdate.setIsOnline(isOnline);
          onlineStatusPublisher.publishStatus(otherUserId, statusUpdate); // Publish TO the peer
          log.trace(
              "Sent status update ({} is {}) to peer {} for connection {}",
              userId,
              isOnline ? "online" : "offline",
              otherUserId,
              connectionId);
        } catch (IllegalArgumentException e) {
          log.warn(
              "Could not find other user for connection {} while broadcasting status for user {}:"
                  + " {}",
              connectionId,
              userId,
              e.getMessage());
        } catch (Exception e) {
          log.error(
              "Error publishing status update for user {} connection {}: {}",
              userId,
              connectionId,
              e.getMessage(),
              e);
        }
      }
    } catch (Exception e) {
      log.error(
          "Error retrieving connections for user {} during broadcast: {}",
          userId,
          e.getMessage(),
          e);
    }
  }

  public boolean isUserOnline(Long userId) {
    return onlineUsers.contains(userId);
  }
}
