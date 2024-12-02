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

  @Enumerated(EnumType.STRING)
  private MessageEventType type; 

  @NotNull
  private Timestamp timestamp;

  public enum MessageEventType {
    SENT, RECEIVED, READ, // REACTIONS - maybe just a couple like iMessages?? 
  }
}
