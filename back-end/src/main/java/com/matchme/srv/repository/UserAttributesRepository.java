package com.matchme.srv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.matchme.srv.model.user.profile.UserGenderType;
import com.matchme.srv.model.user.profile.user_attributes.UserAttributes;

import java.time.LocalDate;
import java.util.List;

public interface UserAttributesRepository extends JpaRepository<UserAttributes, Long> {
    
    /**
     * Find users that match the specified criteria for potential dating matches
     * 
     * @param userId User ID to exclude from results
     * @param genderPreferenceId Gender preference ID
     * @param minBirthDate Minimum birth date (for maximum age)
     * @param maxBirthDate Maximum birth date (for minimum age)
     * @param geoHashes List of geohashes for location matching
     * @param minScore Minimum score threshold
     * @return List of matching user IDs
     */
    @Query(value = 
        "SELECT ua.id FROM user_attributes ua " +
        "JOIN user_score us ON ua.id = us.id " +
        "JOIN user_profile up ON ua.id = up.id " +
        "WHERE ua.id != :userId " +
        "AND ua.gender_id = :genderPreferenceId " +
        "AND ua.birth_date BETWEEN :minBirthDate AND :maxBirthDate " +
        "AND ua.location_geohash IN (:geoHashes) " +
        "AND (us.current_score * us.vibe_probability) >= :minScore " +
        "AND ua.id NOT IN (SELECT connection_id FROM connections WHERE user_id = :userId AND status IN ('MATCHED', 'REJECTED')) " +
        "AND up.active = true",
        nativeQuery = true)
    List<Long> findMatchingUsers(
        @Param("userId") Long userId,
        @Param("genderPreferenceId") Long genderPreferenceId,
        @Param("minBirthDate") LocalDate minBirthDate,
        @Param("maxBirthDate") LocalDate maxBirthDate,
        @Param("geoHashes") List<String> geoHashes,
        @Param("minScore") Integer minScore
    );
}
