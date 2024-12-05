package com.matchme.srv.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.matchme.srv.model.user.profile.ProfileChangeType;

public interface ProfileChangeTypeRepository extends JpaRepository<ProfileChangeType, Long> {
    Optional<ProfileChangeType> findByName(String name);
}
