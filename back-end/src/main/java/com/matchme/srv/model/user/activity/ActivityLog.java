package com.matchme.srv.model.user.activity;

import java.time.Instant;

import com.matchme.srv.model.user.User;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "activity_log")
public class ActivityLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id")
  @NotNull
  private User user;

  @NotNull
  @Enumerated(EnumType.STRING)
  private LogType logType;

  @NotNull
  private Instant instant;

  public enum LogType {
    CREATED, VERIFIED, LOGIN, LOGOUT
  }
  
  public ActivityLog(User user, LogType logType) {
    this.user = user;
    this.logType = logType;
    this.instant = Instant.now();
  }

}
