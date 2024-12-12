package com.matchme.srv.model.user.profile.user_preferences;

import java.util.Set;

import com.matchme.srv.model.user.profile.UserGenderType;
import com.matchme.srv.model.user.profile.UserProfile;

import jakarta.persistence.*;
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
  private UserGenderType gender;


  private Integer age_min;


  private Integer age_max;


  private Integer distance;

  private Double probabilityTolerance;

  @OneToMany(mappedBy = "userPreferences", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<PreferenceChange> preferenceChangeLog;
}
