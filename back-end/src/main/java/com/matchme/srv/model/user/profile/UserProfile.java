package com.matchme.srv.model.user.profile;

import java.util.Set;

import com.matchme.srv.model.connection.DatingPoolSyncListener;
import com.matchme.srv.model.user.User;
import com.matchme.srv.model.user.profile.user_attributes.UserAttributes;
import com.matchme.srv.model.user.profile.user_preferences.UserPreferences;

import jakarta.persistence.*;
import lombok.*;

/**
 * Represents a user's profile in the dating application.
 * This entity contains basic profile information and maintains relationships
 * with
 * detailed user attributes, preferences, and profile changes.
 * 
 * Changes to the profile are automatically synchronized with the dating pool
 * through
 * the DatingPoolSyncListener to ensure matching data consistency.
 */
@Data
@Entity
@EntityListeners(DatingPoolSyncListener.class)
@Table(name = "user_profile")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = { "user", "preferences", "attributes" })
public class UserProfile {

  @Id
  private Long id;

  /**
   * The user associated with this profile.
   * Bidirectional one-to-one relationship with User entity.
   * Maps UserProfile ID directly to the User ID.
   */
  @MapsId
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  /**
   * The user's matching preferences.
   * Contains settings like preferred age range, distance, and gender preferences.
   * Cascade operations ensure preferences are managed with the profile.
   */
  @OneToOne(mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private UserPreferences preferences;

  /**
   * The user's personal attributes.
   * Contains information like gender, birthdate, and location.
   * Cascade operations ensure attributes are managed with the profile.
   */
  @OneToOne(mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private UserAttributes attributes;

  /**
   * Log of changes made to the profile.
   * Tracks modifications for auditing and history purposes.
   */
  @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<ProfileChange> profileChangeLog;

  private String first_name;

  private String last_name;

  private String alias;

  private String city;

  private byte[] profilePicture;

  /**
   * Set of hobbies associated with the user.
   * Used for interest-based matching and profile display.
   */
  @ManyToMany
  @JoinTable(name = "user_profile_hobbies", joinColumns = @JoinColumn(name = "user_profile_id"), inverseJoinColumns = @JoinColumn(name = "hobby_id"))
  private Set<Hobby> hobbies;

  /**
   * Sets the user preferences and maintains the bidirectional relationship.
   *
   * @param preferences The preferences to associate with this profile
   */
  public void setPreferences(UserPreferences preferences) {
    if (preferences != null) {
      preferences.setUserProfile(this);
    }
    this.preferences = preferences;
  }

  /**
   * Sets the user attributes and maintains the bidirectional relationship.
   *
   * @param attributes The attributes to associate with this profile
   */
  public void setAttributes(UserAttributes attributes) {
    if (attributes != null) {
      attributes.setUserProfile(this);
    }
    this.attributes = attributes;
  }

}
