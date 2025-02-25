package com.matchme.srv.model.user.profile.user_preferences;

import java.util.Set;

import com.matchme.srv.model.user.profile.UserGenderType;
import com.matchme.srv.model.user.profile.UserProfile;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "user_preferences")
public class UserPreferences {
  
  @Id // need to look into shared id value with user
  private Long id;

  @MapsId
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private UserProfile userProfile;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "gender_id")
  @NotNull(message = "Gender preference is required")
  private UserGenderType gender;

  @NotNull(message = "Minimum age preference is required")
  @Min(value = 18, message = "Minimum age must be at least 18")
  @Max(value = 100, message = "Minimum age must be at most 100")
  private Integer age_min;

  @NotNull(message = "Maximum age preference is required")
  @Min(value = 18, message = "Maximum age must be at least 18")
  @Max(value = 100, message = "Maximum age must be at most 100")
  private Integer age_max;

  @NotNull(message = "Distance preference is required")
  @Min(value = 1, message = "Distance must be at least 1 km")
  @Max(value = 500, message = "Distance must be at most 500 km")
  private Integer distance;

  @NotNull(message = "Probability tolerance is required")
  @Min(value = 0, message = "Probability tolerance must be at least 0")
  @Max(value = 1, message = "Probability tolerance must be at most 1")
  private Double probability_tolerance;

  @OneToMany(mappedBy = "userPreferences", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<PreferenceChange> preferenceChangeLog;
}
