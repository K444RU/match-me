package com.matchme.srv.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.matchme.srv.model.user.profile.UserProfile;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long>{
  
}
