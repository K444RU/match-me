package com.matchme.srv.ActivityLog;

import java.security.Timestamp;

import com.matchme.srv.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
