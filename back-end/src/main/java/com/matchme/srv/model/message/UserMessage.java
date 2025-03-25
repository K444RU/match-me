package com.matchme.srv.model.message;

import com.matchme.srv.model.connection.Connection;
import com.matchme.srv.model.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "messageEvents")
@Table(name = "user_messages")
public class UserMessage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @NotNull
  @JoinColumn(name = "sender_id")
  private User sender;

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
