package com.matchme.srv.model.user.profile.user_score;

import com.matchme.srv.model.connection.Connection;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "connection_results")
public class ConnectionResult {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private UserScore userScores;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "connection_id")
  private Connection connection;

  private int scoreAfter;

  private int blindAfter;

  private double vibeAfter;

}
