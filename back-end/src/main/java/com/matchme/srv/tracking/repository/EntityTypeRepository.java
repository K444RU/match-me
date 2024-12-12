package com.matchme.srv.tracking.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.matchme.srv.tracking.model.EntityType;

@Repository
public interface EntityTypeRepository extends JpaRepository<EntityType, Long> {
  Optional<EntityType> findByName(String name);
  boolean existsByName(String name);
}
