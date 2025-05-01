package com.matchme.srv.publisher;

import com.matchme.srv.dto.response.ChatMessageResponseDTO;
import com.matchme.srv.dto.response.ChatPreviewResponseDTO;
import com.matchme.srv.dto.response.MessageStatusUpdateDTO;
import java.util.List;
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
public class ChatPublisher {

  private final Map<Long, Sinks.Many<ChatMessageResponseDTO>> messageSinks =
      new ConcurrentHashMap<>();
  private final Map<Long, Sinks.Many<List<ChatPreviewResponseDTO>>> previewSinks =
      new ConcurrentHashMap<>();
  private final Map<Long, Sinks.Many<MessageStatusUpdateDTO>> messageStatusSinks =
      new ConcurrentHashMap<>();

  private Sinks.Many<ChatMessageResponseDTO> getMessageSinkForUser(Long userId) {
    return messageSinks.computeIfAbsent(
        userId, id -> Sinks.many().multicast().onBackpressureBuffer());
  }

  private Sinks.Many<List<ChatPreviewResponseDTO>> getPreviewSinkForUser(Long userId) {
    return previewSinks.computeIfAbsent(
        userId, id -> Sinks.many().multicast().onBackpressureBuffer());
  }

  private Sinks.Many<MessageStatusUpdateDTO> getMessageStatusSinkForUser(Long userId) {
    return messageStatusSinks.computeIfAbsent(
        userId, id -> Sinks.many().multicast().onBackpressureBuffer());
  }

  public Flux<ChatMessageResponseDTO> getMessagePublisher(Long userId, Long connectionId) {
    // Filter the user's stream for the specific connection
    return getMessageSinkForUser(userId)
        .asFlux()
        .filter(msg -> msg.getConnectionId().equals(connectionId));
  }

  public Flux<List<ChatPreviewResponseDTO>> getPreviewPublisher(Long userId) {
    return getPreviewSinkForUser(userId).asFlux();
  }

  public Flux<MessageStatusUpdateDTO> getMessageStatusPublisher(Long userId, Long connectionId) {
    return getMessageStatusSinkForUser(userId)
        .asFlux()
        .filter(status -> status.getConnectionId().equals(connectionId));
  }

  public void publishMessage(Long targetUserId, ChatMessageResponseDTO message) {
    Sinks.EmitResult result = getMessageSinkForUser(targetUserId).tryEmitNext(message);
    if (result.isFailure()) {
      log.error(
          "Failed to publish message {} to user {}: {}",
          message.getMessageId(),
          targetUserId,
          result);
    } else {
      log.debug("Published message {} to user {}", message.getMessageId(), targetUserId);
    }
  }

  public void publishPreviews(Long targetUserId, List<ChatPreviewResponseDTO> previews) {
    Sinks.EmitResult result = getPreviewSinkForUser(targetUserId).tryEmitNext(previews);
    if (result.isFailure()) {
      log.error(
          "Failed to publish {} previews to user {}: {}", previews.size(), targetUserId, result);
    } else {
      log.debug("Published {} previews to user {}", previews.size(), targetUserId);
    }
  }

  public void publishStatusUpdate(Long targetUserId, MessageStatusUpdateDTO statusUpdate) {
    Sinks.EmitResult result = getMessageStatusSinkForUser(targetUserId).tryEmitNext(statusUpdate);
    if (result.isFailure()) {
      log.error(
          "Failed to publish status update ({}) for message {} to user {} for connection {}: {}",
          statusUpdate.getType(),
          statusUpdate.getMessageId(),
          targetUserId,
          statusUpdate.getConnectionId(),
          result);
    } else {
      log.debug(
          "Published status update ({}) for message {} to user {} for connection {}",
          statusUpdate.getType(),
          statusUpdate.getMessageId(),
          targetUserId,
          statusUpdate.getConnectionId());
    }
  }
}
