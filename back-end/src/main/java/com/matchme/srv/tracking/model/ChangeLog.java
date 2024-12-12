package com.matchme.srv.tracking.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "change_logs")
@Data
public class ChangeLog {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "entity_type_id", nullable = false)
  private EntityType entityType;

  @Column(nullable = false)
  private Long entityId;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private ChangeOperation changeType;

  @Column(columnDefinition = "jsonb")
  private String oldValue;

  @Column(columnDefinition = "jsonb")
  private String newValue;

  @Column(nullable = false)
  private Long userId;

  @Column(nullable = false)
  private Instant timestamp;
}
