package com.matchme.srv.model.message;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@AllArgsConstructor
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

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "message_event_type", length = 8)
  private MessageEventTypeEnum messageEventType;

  @NotNull private Instant timestamp;
}
