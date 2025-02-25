package com.matchme.srv.model.user.profile.user_score;

import java.util.Set;

import com.matchme.srv.model.user.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "user_scores")
public class UserScore {

  @Id
  private Long id;

  @MapsId
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  @NotNull(message = "User is required")
  private User user;

  @Min(value = 0, message = "Current score must be at least 0")
  @Max(value = 3000, message = "Current score must be at most 3000")
  private int currentScore; // ELO of yourself

  @NotNull(message = "Vibe probability is required")
  @Min(value = 0, message = "Vibe probability must be at least 0")
  @Max(value = 1, message = "Vibe probability must be at most 1")
  private double vibeProbability; 

  @Min(value = 0, message = "Current blind must be at least 0")
  @Max(value = 3000, message = "Current blind must be at most 3000")
  private int currentBlind; // ELO of preference
  
  @Min(value = 0, message = "Activity level must be at least 0")
  @Max(value = 10, message = "Activity level must be at most 10")
  private int activityLevel; // User activity level from 0-10

  @OneToMany(mappedBy = "userScores", cascade = CascadeType.ALL)
  private Set<ConnectionResult> results;
  
  public UserScore() {
    this.currentScore = 1000;
    this.vibeProbability = 1;
    this.currentBlind = 1000;
    this.activityLevel = 5; // Default activity level
  }

}
