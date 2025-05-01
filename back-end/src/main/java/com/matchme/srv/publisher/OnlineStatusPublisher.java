package com.matchme.srv.publisher;

import com.matchme.srv.dto.graphql.OnlineStatusEvent;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Service
@RequiredArgsConstructor
@Slf4j
public class OnlineStatusPublisher {
  private final Map<Long, Sinks.Many<OnlineStatusEvent>> sinks = new ConcurrentHashMap<>();

  private Sinks.Many<OnlineStatusEvent> getSinkForUser(Long userId) {
    return sinks.computeIfAbsent(userId, id -> Sinks.many().multicast().onBackpressureBuffer());
  }

  public Flux<OnlineStatusEvent> getPublisher(Long userId, Long connectionId) {
    // Filter online status updates for the specific connection
    return getSinkForUser(userId)
        .asFlux()
        .filter(status -> status.getConnectionId().equals(connectionId));
  }

  public void publishStatus(Long targetUserId, OnlineStatusEvent status) {
    Sinks.EmitResult result = getSinkForUser(targetUserId).tryEmitNext(status);
    if (result.isFailure()) {
      log.error(
          "Failed to publish online status ({}) for user {} to user {} for connection {}: {}",
          status.getIsOnline(),
          status.getUserId(),
          targetUserId,
          status.getConnectionId(),
          result);
    } else {
      log.debug(
          "Published online status ({}) for user {} to user {} for connection {}",
          status.getIsOnline(),
          status.getUserId(),
          targetUserId,
          status.getConnectionId());
    }
  }
}
