package com.matchme.srv.ActivityLog;

import java.sql.Timestamp;

import com.matchme.srv.user.User;

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
  private LogType logType;

  @NotNull
  private Timestamp timestamp;

  public enum LogType {
    LOGIN, LOGOUT
  }
}
