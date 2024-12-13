package com.matchme.srv.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.matchme.srv.model.user.profile.user_preferences.PreferenceChangeType;

public interface PreferenceChangeTypeRepository extends JpaRepository<PreferenceChangeType, Long> {
    Optional<PreferenceChangeType> findByName(String name);
}
