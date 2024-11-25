package com.matchme.srv.states;

import java.sql.Timestamp;
import com.matchme.srv.connection.Connection;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "connection_log")
public class ConnectionState {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "connection_id")
  @NotNull
  private Connection connection;

  @Enumerated(EnumType.STRING)
  private ConnectionType type;

  @NotNull
  private Timestamp timestamp;

  public enum ConnectionType {
    SEEN, OPENED_PROFILE, JUST_FRIENDS, MAYBE_MORE, INTERESTED, CLOSED
  }
}
