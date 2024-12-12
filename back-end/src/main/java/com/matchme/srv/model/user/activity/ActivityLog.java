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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "activity_log_type_id")
  private ActivityLogType type;

  @NotNull
  private Instant instant;
  
  public ActivityLog(User user, ActivityLogType logType) {
    this.user = user;
    this.type = logType;
    this.instant = Instant.now();
  }

  public ActivityLog() {

  }
}
