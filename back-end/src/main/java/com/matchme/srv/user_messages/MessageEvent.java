package com.matchme.srv.user_messages;

import java.security.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "message_events")
public class MessageEvent {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "message_id", nullable = false)
  private UserMessage message;

  @Enumerated(EnumType.STRING)
  private MessageEventType type; 

  @Column(nullable = false)
  private Timestamp timestamp;

  public enum MessageEventType {
    SENT, RECEIVED, READ, // REACTIONS - maybe just a couple like iMessages?? 
  }
}
