package com.matchme.srv.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.matchme.srv.model.connection.DatingPool;

/**
 * Repository interface for managing DatingPool entities.
 * Provides methods for querying and retrieving potential matches based on user
 * preferences and attributes.
 * Implements custom queries optimized for matching operations.
 */
public interface MatchingRepository extends JpaRepository<DatingPool, Long> {

    Optional<DatingPool> findById(Long profileId);

    /**
     * Finds potential matches based on specified matching parameters.
     * The query filters users based on:
     * <ul>
     * <li>Gender preferences (mutual compatibility)</li>
     * <li>Age range preferences (mutual compatibility)</li>
     * <li>Geographic location (using geohash matching)</li>
     * </ul>
     *
     * @param gender           The gender identifier being searched for
     * @param lookingForGender The gender identifier that should be looking for the
     *                         user's gender
     * @param userAge          The age of the user searching for matches
     * @param minAge           The minimum age preference set by the user
     * @param maxAge           The maximum age preference set by the user
     * @param locations        Set of geohash areas within the user's preferred
     *                         distance
     * @param userLocation     The user's current geohash location
     * @param precision        The precision of the geohash
     * @return List of dating pool entries matching the specified criteria
     */
    @Query("""
            SELECT dp FROM DatingPool dp WHERE
            dp.myGender = :gender AND
            dp.myAge BETWEEN :minAge AND :maxAge AND
            substring(dp.myLocation, 1, :precision) IN :locations AND
            dp.lookingForGender = :lookingForGender AND
            :userAge BETWEEN dp.ageMin AND dp.ageMax AND
            substring(:userLocation, 1, :precision) MEMBER OF dp.suitableGeoHashes
            """)
    List<DatingPool> findUsersThatMatchParameters(
            @Param("gender") Long gender,
            @Param("lookingForGender") Long lookingForGender,
            @Param("userAge") Integer userAge,
            @Param("minAge") Integer minAge,
            @Param("maxAge") Integer maxAge,
            @Param("locations") Set<String> locations,
            @Param("userLocation") String userLocation,
            @Param("precision") int precision);
}