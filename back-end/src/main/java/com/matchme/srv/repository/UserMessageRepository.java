package com.matchme.srv.repository;

import com.matchme.srv.model.message.UserMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMessageRepository extends JpaRepository<UserMessage, Long> {

    Page<UserMessage> findByConnectionIdOrderByCreatedAtDesc(Long connectionId, Pageable pageable);

    UserMessage findTopByConnectionIdOrderByCreatedAtDesc(Long connectionId);

    @Query("""
        SELECT COUNT(msg)
        FROM UserMessage msg
        WHERE msg.connection.id = :connectionId
          AND msg.user.id <> :userId
          AND NOT EXISTS (
            SELECT 1
            FROM MessageEvent ev
            WHERE ev.message = msg
              AND ev.messageEventType.name = 'READ'
          )
    """)
    int countUnreadMessages(@Param("connectionId") Long connectionId,
                            @Param("userId") Long userId);
}

