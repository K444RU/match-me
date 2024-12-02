package com.matchme.srv.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.matchme.srv.model.user.profile.user_attributes.UserAttributes;

public interface UserAttributesRepository extends JpaRepository<UserAttributes, Long> {
  
}
