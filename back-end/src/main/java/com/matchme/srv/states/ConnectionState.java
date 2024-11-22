package com.matchme.srv.states;

import java.security.Timestamp;
import java.sql.Connection;

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
@Table(name = "connection_log")
public class ConnectionState {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "connection_id", nullable = false)
  private Connection connection;

  @Enumerated(EnumType.STRING)
  private ConnectionType type;

  @Column(nullable = false)
  private Timestamp timestamp;

  public enum ConnectionType {
    SEEN, OPENED_PROFILE, JUST_FRIENDS, MAYBE_MORE, INTERESTED, CLOSED
  }
}
