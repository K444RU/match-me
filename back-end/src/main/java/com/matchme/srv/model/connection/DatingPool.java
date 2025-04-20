package com.matchme.srv.model.connection;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.matchme.srv.model.user.profile.UserGenderEnum;
import com.matchme.srv.validation.annotations.ValidGender;

import jakarta.persistence.Index;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a user's dating pool entry with denormalized data for efficient
 * matching.
 * This entity is optimized for high-performance queries without requiring joins
 * to related tables.
 * It maintains a cached, flattened view of user attributes, preferences, and
 * matching criteria.
 *
 * The entity uses second-level caching to improve read performance and includes
 * indexes on frequently queried fields for optimal matching operations.
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE) // Enable second-level caching
@Table(name = "dating_pool", indexes = {
        @Index(name = "idx_dating_pool_gender", columnList = "my_gender"),
        @Index(name = "idx_dating_pool_looking_for", columnList = "looking_for_gender"),
        @Index(name = "idx_dating_pool_age", columnList = "my_age"),
        @Index(name = "idx_dating_pool_location", columnList = "my_location"),
        @Index(name = "idx_dating_pool_score", columnList = "actual_score")
})
public class DatingPool {

    /**
     * The unique identifier for this dating pool entry.
     * Maps directly to the user's profile ID.
     */
    @Id
    @Column(name = "profile_id")
    private Long profileId;

    /**
     * The user's gender identifier.
     * Used for primary matching criteria.
     */
    @NotNull(message = "Gender is required")
    @Column(name = "my_gender", nullable = false)
    @Enumerated(EnumType.STRING)
    @ValidGender(message = "Invalid gender value")
    private UserGenderEnum myGender;

    /**
     * The gender identifier that the user is interested in matching with.
     * Used for primary matching criteria.
     */
    @NotNull(message = "Looking for gender is required")
    @Column(name = "looking_for_gender", nullable = false)
    @Enumerated(EnumType.STRING)
    @ValidGender(message = "Invalid gender value")
    private UserGenderEnum lookingForGender;

    /**
     * The user's current age.
     * Calculated from birthdate and updated periodically.
     */
    @NotNull(message = "Age is required")
    @Min(value = 18, message = "Age must be at least 18")
    @Max(value = 121, message = "Age must be less than 121")
    @Column(name = "my_age", nullable = false)
    private Integer myAge;

    /**
     * The minimum age preference for potential matches.
     */
    @NotNull(message = "Minimum age is required")
    @Min(value = 18, message = "Minimum age must be at least 18")
    @Max(value = 121, message = "Minimum age must be less than 121")
    @Column(name = "age_min", nullable = false)
    private Integer ageMin;

    /**
     * The maximum age preference for potential matches.
     */
    @NotNull(message = "Maximum age is required")
    @Min(value = 18, message = "Maximum age must be at least 18")
    @Max(value = 121, message = "Maximum age must be less than 121")
    @Column(name = "age_max", nullable = false)
    private Integer ageMax;

    /**
     * The user's current location as a geohash.
     * Stored as a 6-7 character precision geohash for efficient proximity searches.
     * MVP stores only with 6 character precision
     */
    @NotNull(message = "Location is required")
    @Pattern(regexp = "^[0-9b-hjkmnp-z]{6,7}$", message = "Location must be a valid geohash of length 6-7")
    @Column(name = "my_location", nullable = false)
    private String myLocation; // Geohash of 6-7 length

    /**
     * The user's current matching score.
     * Used in the matching algorithm to determine compatibility and ranking.
     */
    @NotNull(message = "Score is required")
    @Column(name = "actual_score", nullable = false)
    private Integer actualScore;

    /**
     * Set of geohash areas within the user's preferred matching distance.
     * Pre-calculated for efficient proximity matching without runtime calculations.
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "dating_pool_geo_hashes", joinColumns = @JoinColumn(name = "dating_pool_id"), indexes = @Index(name = "idx_geo_hash", columnList = "geo_hash"))
    @Column(name = "geo_hash")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @Builder.Default
    private Set<String> suitableGeoHashes = new HashSet<>();

    /**
     * Set of hobby IDs associated with the user.
     * Used for interest-based matching and filtering.
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "dating_pool_hobbies", joinColumns = @JoinColumn(name = "dating_pool_id"), indexes = @Index(name = "idx_hobby_id", columnList = "hobby_id"))
    @Column(name = "hobby_id")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @Builder.Default
    private Set<Long> hobbyIds = new HashSet<>();

}