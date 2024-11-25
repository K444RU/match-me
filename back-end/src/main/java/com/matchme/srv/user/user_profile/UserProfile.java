package com.matchme.srv.user.user_profile;

import java.util.Set;

import com.matchme.srv.user.User;

import jakarta.persistence.*;
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

  // TODO: Find a solution so that we can signup - I think an easy solution would be to create the profile when we get the email verification token. 
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
