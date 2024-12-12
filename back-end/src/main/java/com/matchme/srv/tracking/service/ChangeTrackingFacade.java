package com.matchme.srv.tracking.service;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matchme.srv.tracking.model.ChangeLog;
import com.matchme.srv.tracking.model.ChangeOperation;
import com.matchme.srv.tracking.model.EntityType;
import com.matchme.srv.tracking.repository.ChangeLogRepository;
import com.matchme.srv.tracking.repository.EntityTypeRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChangeTrackingFacade {
  private final ChangeLogRepository changeLogRepository;
  private final EntityTypeRepository entityTypeRepository;
  private final ObjectMapper objectMapper;
  private final AsyncChangeProcessor asyncChangeProcessor;

  @Transactional // all or nothing, db consistency, automatic rollback on exceptions
  public <T> void trackChange(
    String entityTypeName,
    Long entityId,
    ChangeOperation operation,
    T oldValue,
    T newValue, 
    Long userId
  ) {
    try {
      EntityType entityType = entityTypeRepository.findByName(entityTypeName)
        .orElseThrow(() -> new IllegalArgumentException("Invalid entity type: " + entityTypeName));
      
      ChangeLog log = new ChangeLog();
      log.setEntityType(entityType);
      log.setEntityId(entityId);
      log.setChangeType(operation);
      log.setOldValue(oldValue != null ? objectMapper.writeValueAsString(oldValue) : null);
      log.setNewValue(newValue != null ? objectMapper.writeValueAsString(newValue) : null);
      log.setUserId(userId);
      log.setTimestamp(Instant.now());

      changeLogRepository.save(log);

      asyncChangeProcessor.processChange(log);
    } catch (JsonProcessingException e) {
      log.error("Failed to track change for entity type: {}, id {}", entityTypeName, entityId, e);
      throw new RuntimeException("Failed to track change", e);
    }
  }

  // public List<ChangeLog> getChangeHistory(String entityTypeName, Long entityId) {
  //   EntityType entityType = entityTypeRepository.findByName(entityTypeName)
  //     .orElseThrow(() -> new IllegalArgumentException("Invalid entity type: " + entityTypeName));
  //   return changeLogRepository.findByEntityTypeAndEntityId(entityType, entityId);
  // }

  // public List<ChangeLog> getLatestChanges(String entityTypeName, Long entityId, int limit) {
  //   EntityType entityType = entityTypeRepository.findByName(entityTypeName)
  //     .orElseThrow(() -> new IllegalArgumentException("Invalid entity type: " + entityTypeName));
  //   return changeLogRepository.findLatestChanges(entityType, entityId, PageRequest.of(0, limit));
  // }

}
