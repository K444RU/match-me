package com.matchme.srv.model.user.profile.user_attributes;

import java.sql.Timestamp;

import com.matchme.srv.model.user.profile.user_preferences.UserPreferences;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "user_attribute_changes")
public class AttributeChange {
  
  @Id
  @GeneratedValue( strategy = GenerationType.IDENTITY) 
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @NotNull
  @JoinColumn(name = "user_attributes_id")
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
