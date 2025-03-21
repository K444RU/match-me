package com.matchme.srv.model.user.profile.user_preferences;

import java.time.Instant;

import jakarta.persistence.*;
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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "preference_change_type_id")
  private PreferenceChangeType type;

  @NotNull
  private Instant instant;

  private String content; 

  public PreferenceChange() {}

  public PreferenceChange(UserPreferences preferences, PreferenceChangeType type, String content) {
    this.userPreferences = preferences;
    this.type = type;
    this.content = content;
    this.instant = Instant.now();
  }
}
