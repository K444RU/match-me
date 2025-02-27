package com.matchme.srv.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.matchme.srv.model.connection.DatingPool;

public interface MatchingRepository extends JpaRepository<DatingPool, Long> {

    Optional<DatingPool> findById(Long userId);

    @Query("""
            SELECT dp FROM DatingPool dp WHERE
            dp.myGender = :gender AND
            dp.myAge BETWEEN :minAge AND :maxAge AND
            dp.myLocation IN :locations AND
            dp.lookingFor = :lookingForGender AND
            :userAge BETWEEN dp.ageMin AND dp.ageMax AND
            :userLocation MEMBER OF dp.suitableGeoHashes
            """)
    List<DatingPool> findUsersThatMatchParameters(
            @Param("gender") Long gender,
            @Param("lookingForGender") Long lookingForGender,
            @Param("userAge") Integer userAge,
            @Param("minAge") Integer minAge,
            @Param("maxAge") Integer maxAge,
            @Param("locations") Set<String> locations,
            @Param("userLocation") String userLocation);
}