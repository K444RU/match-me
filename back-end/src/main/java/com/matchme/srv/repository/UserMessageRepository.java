package com.matchme.srv.repository;

import com.matchme.srv.model.message.UserMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMessageRepository extends JpaRepository<UserMessage, Long> {

    Page<UserMessage> findByConnectionIdOrderByCreatedAtDesc(Long connectionId, Pageable pageable);
}
