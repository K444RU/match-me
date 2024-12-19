package com.matchme.srv.model.message;

import com.matchme.srv.model.connection.Connection;
import com.matchme.srv.model.user.User;

import java.sql.Timestamp;
import java.util.Set;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "user_messages")
public class UserMessage {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @NotNull
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @NotNull
  @JoinColumn(name = "connection_id")
  private Connection connection;

  @NotNull
  private String content;

  @Column(name = "created_at")
  private Timestamp createdAt;

  @OneToMany(mappedBy = "message", cascade = CascadeType.ALL)
  private Set<MessageEvent> messageEvents; 
}
