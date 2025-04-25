package com.matchme.srv.repository;

import com.matchme.srv.model.connection.DismissedRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DismissRecommendationRepository extends JpaRepository<DismissedRecommendation, Long> {

    @Query("SELECT dr.dismissedUserProfile.id FROM DismissedRecommendation dr WHERE dr.userProfile.id = :userProfileId")
    List<Long> findDismissedRecommendationIdByProfileId(@Param("userProfileId") Long userProfileId);
}
