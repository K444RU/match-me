package com.matchme.srv.model.message;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
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

  @NotNull private Instant timestamp;
}
