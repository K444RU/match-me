package com.matchme.srv.model.message;

import java.sql.Timestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
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
  private MessageEventType message_event_type;

  @NotNull
  private Timestamp timestamp;
}
