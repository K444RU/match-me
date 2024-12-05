package com.matchme.srv.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.matchme.srv.model.user.UserStateTypes;

public interface UserStateTypesRepository extends JpaRepository<UserStateTypes, Long> {
    Optional<UserStateTypes> findByName(String name);
}
