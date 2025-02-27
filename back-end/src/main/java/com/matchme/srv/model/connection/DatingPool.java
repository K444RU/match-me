package com.matchme.srv.model.connection;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import jakarta.persistence.Index;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
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
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE) // Enable second-level caching
@Table(name = "dating_pool", indexes = {
        @Index(name = "idx_dating_pool_gender", columnList = "my_gender_id"),
        @Index(name = "idx_dating_pool_looking_for", columnList = "looking_for_gender_id"),
        @Index(name = "idx_dating_pool_age", columnList = "my_age"),
        @Index(name = "idx_dating_pool_location", columnList = "my_location"),
        @Index(name = "idx_dating_pool_score", columnList = "actual_score")
})
public class DatingPool {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @NotNull(message = "Gender is required")
    @Column(name = "my_gender_id", nullable = false)
    private Long myGender;

    @NotNull(message = "Looking for gender is required")
    @Column(name = "looking_for_gender_id", nullable = false)
    private Long lookingForGender;

    @NotNull(message = "Age is required")
    @Min(value = 18, message = "Age must be at least 18")
    @Max(value = 121, message = "Age must be less than 121")
    @Column(name = "my_age", nullable = false)
    private Integer myAge;

    @NotNull(message = "Minimum age is required")
    @Min(value = 18, message = "Minimum age must be at least 18")
    @Max(value = 121, message = "Minimum age must be less than 121")
    @Column(name = "age_min", nullable = false)
    private Integer ageMin;

    @NotNull(message = "Maximum age is required")
    @Min(value = 18, message = "Maximum age must be at least 18")
    @Max(value = 121, message = "Maximum age must be less than 121")
    @Column(name = "age_max", nullable = false)
    private Integer ageMax;

    @NotNull(message = "Location is required")
    @Pattern(regexp = "^[0-9b-hjkmnp-z]{6,7}$", message = "Location must be a valid geohash of length 6-7")
    @Column(name = "my_location", nullable = false)
    private String myLocation; // Geohash of 6-7 length

    @NotNull(message = "Score is required")
    @Column(name = "actual_score", nullable = false)
    private Integer actualScore;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "dating_pool_geo_hashes", joinColumns = @JoinColumn(name = "dating_pool_id"), indexes = @Index(name = "idx_geo_hash", columnList = "geo_hash"))
    @Column(name = "geo_hash")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @Builder.Default
    private Set<String> suitableGeoHashes = new HashSet<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "dating_pool_hobbies", joinColumns = @JoinColumn(name = "dating_pool_id"), indexes = @Index(name = "idx_hobby_id", columnList = "hobby_id"))
    @Column(name = "hobby_id")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @Builder.Default
    private Set<Long> hobbyIds = new HashSet<>();

}