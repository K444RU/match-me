package com.matchme.srv.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.matchme.srv.model.user.activity.ActivityLogType;

public interface ActivityLogTypeRepository extends JpaRepository<ActivityLogType, Long> {
    Optional<ActivityLogType> findByName(String name);
}
