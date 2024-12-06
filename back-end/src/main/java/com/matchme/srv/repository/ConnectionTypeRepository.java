package com.matchme.srv.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.matchme.srv.model.connection.ConnectionType;

public interface ConnectionTypeRepository extends JpaRepository<ConnectionType, Long> {
  Optional<ConnectionType> findByName(String name);
}
