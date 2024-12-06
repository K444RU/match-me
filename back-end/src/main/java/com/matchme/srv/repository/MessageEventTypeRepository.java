package com.matchme.srv.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.matchme.srv.model.message.MessageEventType;

public interface MessageEventTypeRepository extends JpaRepository<MessageEventType, Long> {
  Optional<MessageEventType> findByName(String name);
}
