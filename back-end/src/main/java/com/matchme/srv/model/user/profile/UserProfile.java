package com.matchme.srv.model.user.profile;

import java.util.Set;

import com.matchme.srv.model.user.User;
import com.matchme.srv.model.user.profile.user_attributes.UserAttributes;
import com.matchme.srv.model.user.profile.user_preferences.UserPreferences;

import jakarta.persistence.*;
// import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "user_profile")
public class UserProfile {

  @Id // need to look into shared id value with user
  private Long id;

  @MapsId
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  // TODO: Find a solution so that we can signup - I think an easy solution would be to create the profile when we get the email verification token. 
  // Temporarily I have commented the @NotNull's out.
  // Because with the current logic, we would have to set everything up in the initial popup.
  // Otherwise the notnull's were fucking us.
  @OneToOne(mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  // @NotNull
  private UserPreferences preferences;

  @OneToOne(mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  // @NotNull
  private UserAttributes attributes;

  @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<ProfileChange> profileChangeLog;
  
  public void setPreferences(UserPreferences preferences) {
    if (preferences != null) {
      preferences.setUserProfile(this);
    }
    this.preferences = preferences;
  }

  public void setAttributes(UserAttributes attributes) {
    if (attributes != null) {
      attributes.setUserProfile(this);
    }
    this.attributes = attributes;
  }
}
