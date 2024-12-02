package com.matchme.srv.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.matchme.srv.model.user.UserAuth;

public interface UserAuthRepository extends JpaRepository<UserAuth, Long> {
  
}
