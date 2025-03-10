package com.matchme.srv.model.user.profile.user_preferences;

import java.util.Set;

import com.matchme.srv.model.connection.DatingPoolSyncListener;
import com.matchme.srv.model.user.profile.UserGenderType;
import com.matchme.srv.model.user.profile.UserProfile;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Represents a user's matching preferences in the dating application.
 * This entity stores various criteria that determine potential matches,
 * including age range, distance preferences, and gender preferences.
 * 
 * Changes to preferences are automatically synchronized with the dating pool
 * through the DatingPoolSyncListener to ensure matching data consistency.
 */
@Data
@Entity
@EntityListeners(DatingPoolSyncListener.class)
@Table(name = "user_preferences")
public class UserPreferences {

    @Id
    private Long id;

    /**
     * The user profile associated with these preferences.
     * Bidirectional one-to-one relationship with UserProfile entity.
     * Maps UserPreferences ID directly to the user profile's ID.
     */
    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserProfile userProfile;

    /**
     * The gender type that the user is interested in matching with.
     * References the UserGenderType entity for standardized gender options.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gender_id")
    private UserGenderType gender;

    /**
     * The minimum age preference for potential matches.
     * Must be at least 18 years old.
     */
    private Integer ageMin;

    /**
     * The maximum age preference for potential matches.
     * Must be less than 121 years old.
     */
    private Integer ageMax;

    /**
     * The maximum distance (in kilometers) the user is willing to match within.
     * Used for location-based matching using geohash calculations.
     */
    private Integer distance;

    /**
     * The user's tolerance for probability-based matching.
     * Values range from 0.0 to 1.0, with higher values indicating more lenient
     * matching criteria. Not Used in MVP.
     */
    private Double probabilityTolerance;

    /**
     * Log of changes made to the preferences.
     * Tracks modifications for auditing and history purposes.
     */
    @OneToMany(mappedBy = "userPreferences", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<PreferenceChange> preferenceChangeLog;
}
