package com.matchme.srv.repository;

import com.matchme.srv.model.message.MessageEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageEventRepository extends JpaRepository<MessageEvent, Long> {}
