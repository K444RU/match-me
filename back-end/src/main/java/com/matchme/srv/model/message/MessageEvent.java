package com.matchme.srv.model.message;

import java.time.Instant;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(exclude = "message")
@Table(name = "message_events")
public class MessageEvent {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @NotNull
  @JoinColumn(name = "message_id")
  private UserMessage message;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "message_event_type_id")
  private MessageEventType messageEventType;

  @NotNull
  private Instant timestamp;
}
