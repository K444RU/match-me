package com.matchme.srv.publisher;

import com.matchme.srv.dto.graphql.TypingStatusEvent;
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
public class TypingStatusPublisher {
  private final Map<Long, Sinks.Many<TypingStatusEvent>> sinks = new ConcurrentHashMap<>();

  private Sinks.Many<TypingStatusEvent> getSinkForUser(Long userId) {
    return sinks.computeIfAbsent(userId, id -> Sinks.many().multicast().onBackpressureBuffer());
  }

  public Flux<TypingStatusEvent> getPublisher(Long userId) {
    return getSinkForUser(userId)
        .asFlux()
        .filter(status -> !status.getSenderId().equals(userId));
  }

  public void publishStatus(Long targetUserId, TypingStatusEvent status) {
    Sinks.EmitResult result = getSinkForUser(targetUserId).tryEmitNext(status);
    if (result.isFailure()) {
      log.error(
          "Failed to publish typing status from {} to user {} for connection {}: {}",
          status.getSenderId(),
          targetUserId,
          status.getConnectionId(),
          result);
    } else {
      log.debug(
          "Published typing status from {} to user {} for connection {}",
          status.getSenderId(),
          targetUserId,
          status.getConnectionId());
    }
  }
}
