package com.matchme.srv.user.user_profile;

import java.util.Set;

import com.matchme.srv.user.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
// import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "user_profile")
public class UserProfile {

  @Id // need to look into shared id value with user
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(mappedBy = "userProfile", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private User user;

  // TODO: Find a solution so that we can signup
  // Temporarily I have commented the @NotNull's out.
  // Because with the current logic, we would have to set everything up in the initial popup.
  // Otherwise the notnull's were fucking us.
  @OneToOne(fetch = FetchType.LAZY)
  // @NotNull
  @JoinColumn(name = "user_preferences_id")
  private UserPreferences userPreferences;

  @OneToOne
  // @NotNull
  @JoinColumn(name = "user_attributes")
  private UserAttributes userAttributes;


  @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<ProfileChange> profileChangeLog;
  
}
