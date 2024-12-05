package com.matchme.srv.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.matchme.srv.model.user.profile.Gender;

public interface GenderRepository extends JpaRepository<Gender, Long> {
    Optional<Gender> findByName(String name);
}
