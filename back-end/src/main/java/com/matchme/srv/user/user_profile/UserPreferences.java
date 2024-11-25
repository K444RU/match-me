package com.matchme.srv.user.user_profile;

import java.util.Set;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "user_preferences")
public class UserPreferences {
  
  @Id // need to look into shared id value with user
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  private Gender gender;

  @NotNull
  private Integer age_min;

  @NotNull
  private Integer age_max;

  @NotNull
  private Integer distance;

  private Integer blind;

  @OneToMany(mappedBy = "userPreferences", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<PreferenceChange> preferenceChangeLog;
}
