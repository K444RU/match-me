package com.matchme.srv.model.user.profile.user_attributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.matchme.srv.model.connection.UserAttributesListener;
import com.matchme.srv.model.user.profile.ProfileChange;
import com.matchme.srv.model.user.profile.UserGenderType;
import com.matchme.srv.model.user.profile.UserProfile;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

/**
 * Represents a user's personal attributes in the dating application.
 * This entity stores essential user characteristics such as gender, birthdate,
 * and location that are used for matching purposes.
 * 
 * Changes to attributes are automatically synchronized with the dating pool
 * through the DatingPoolSyncListener to ensure matching data consistency.
 */
@Data
@Entity
@EntityListeners(UserAttributesListener.class)
@Table(name = "user_attributes")
@ToString(exclude = "userProfile")
public class UserAttributes {

  @Id
  private Long id;

  /**
   * The user profile associated with these attributes.
   * Bidirectional one-to-one relationship with UserProfile entity.
   * Maps UserAttributes ID directly to the user profile's ID.
   */
  @MapsId
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private UserProfile userProfile;

  /**
   * The user's gender.
   * References the UserGenderType entity for standardized gender options.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "gender_id")
  private UserGenderType gender;

  /**
   * The user's date of birth.
   * Used for age calculation and age-based matching.
   */
  private LocalDate birthdate;

  /**
   * The user's geographical location stored as coordinates.
   * List contains [longitude, latitude] used for location-based matching.
   */
  private List<Double> location = new ArrayList<>();

  /**
   * Log of changes made to the attributes.
   * Tracks modifications for auditing and history purposes.
   */
  @OneToMany(mappedBy = "userAttributes", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<ProfileChange> attributeChangeLog;
}
