package com.matchme.srv.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.matchme.srv.model.user.profile.UserGenderType;

public interface UserGenderTypeRepository extends JpaRepository<UserGenderType, Long> {
    Optional<UserGenderType> findByName(String name);
}
