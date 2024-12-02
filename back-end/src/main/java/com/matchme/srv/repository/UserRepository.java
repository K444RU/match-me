package com.matchme.srv.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.matchme.srv.model.user.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);

}
