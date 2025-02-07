package com.matchme.srv.model.message;

import com.matchme.srv.model.connection.Connection;
import com.matchme.srv.model.user.User;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Builder
@EqualsAndHashCode(exclude = "messageEvents")
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
  @Column(columnDefinition = "text")
  private String content;

  @Column(name = "created_at")
  private Instant createdAt;

  @OneToMany(mappedBy = "message", cascade = CascadeType.ALL)
  @Builder.Default
  private Set<MessageEvent> messageEvents = new HashSet<>();
}
