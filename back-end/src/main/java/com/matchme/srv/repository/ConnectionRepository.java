package com.matchme.srv.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.matchme.srv.model.connection.DatingPool;

public interface ConnectionRepository extends JpaRepository<DatingPool, Long> {
  
}
