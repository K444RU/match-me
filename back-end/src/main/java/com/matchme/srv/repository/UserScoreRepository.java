package com.matchme.srv.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.matchme.srv.model.user.profile.user_score.UserScore;

public interface UserScoreRepository extends JpaRepository<UserScore, Long> {
  
}
