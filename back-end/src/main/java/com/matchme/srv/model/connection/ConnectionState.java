package com.matchme.srv.model.connection;

import com.matchme.srv.model.enums.ConnectionStatus;
import com.matchme.srv.model.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "connection_log")
public class ConnectionState {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "connection_id")
  @NotNull
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private Connection connection;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  @NotNull
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "connection_type_id")
  private ConnectionType connection_type;

  @NotNull
  private LocalDateTime timestamp;

  @Column(name = "status", nullable = false)
  @Enumerated(EnumType.STRING)
  private ConnectionStatus status;

  @Column(name = "requester_id")
  private Long requesterId;

  @Column(name = "target_id")
  private Long targetId;

}
