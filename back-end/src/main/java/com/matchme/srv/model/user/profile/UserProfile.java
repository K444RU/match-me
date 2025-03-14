package com.matchme.srv.model.user.profile;

import java.util.Set;

import com.matchme.srv.model.user.User;
import com.matchme.srv.model.user.profile.user_attributes.UserAttributes;
import com.matchme.srv.model.user.profile.user_preferences.UserPreferences;

import jakarta.persistence.*;
// import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Entity
@Table(name = "user_profile")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = {"user", "preferences", "attributes"})
public class UserProfile {

  @Id
  private Long id;

  @MapsId
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @OneToOne(mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  // @NotNull
  private UserPreferences preferences;

  @OneToOne(mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  // @NotNull
  private UserAttributes attributes;

  @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<ProfileChange> profileChangeLog;
  
  private String first_name;

  private String last_name;

  private String alias;

  private String city;

  private byte[] profilePicture;

  @ManyToMany
  @JoinTable(name = "user_profile_hobbies",
          joinColumns = @JoinColumn(name = "user_profile_id"),
          inverseJoinColumns = @JoinColumn(name = "hobby_id"))
  private Set<Hobby> hobbies;

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
