package com.matchme.srv.user_profile;

import java.security.Timestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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
@Table(name = "user_preference_changes")
public class PreferenceChange {
  
  @Id
  @GeneratedValue( strategy = GenerationType.IDENTITY) 
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @NotNull
  @JoinColumn(name = "user_preferences_id")
  private UserPreferences userPreferences;

  @Enumerated(EnumType.STRING)
  private PreferenceChangeType type;

  @NotNull
  private Timestamp timestamp;

  @NotNull 
  private String newState; 

  public enum PreferenceChangeType {
    GENDER, AGE_MIN, AGE_MAX, DISTANCE, BLIND //maybe blind shouldn't be here? 
  }
}
