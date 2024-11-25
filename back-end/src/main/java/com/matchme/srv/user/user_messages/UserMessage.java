package com.matchme.srv.user.user_messages;

import com.matchme.srv.connection.Connection;
import java.util.Set;

import com.matchme.srv.user.User;

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

  @OneToMany(mappedBy = "", cascade = CascadeType.ALL)
  private Set<MessageEvent> messageEvents; 
}
