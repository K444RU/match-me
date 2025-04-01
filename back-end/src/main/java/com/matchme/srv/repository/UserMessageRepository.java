package com.matchme.srv.repository;

import com.matchme.srv.model.message.UserMessage;

import java.util.List;

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

  @Query(
      """
          SELECT COUNT(msg)
          FROM UserMessage msg
          WHERE msg.connection.id = :connectionId
            AND msg.sender.id <> :userId
            AND NOT EXISTS (
              SELECT 1
              FROM MessageEvent ev
              WHERE ev.message = msg
                AND ev.messageEventType.name = 'READ'
            )
      """)
  int countUnreadMessages(@Param("connectionId") Long connectionId, @Param("userId") Long userId);

    /**
   * Finds messages in a connection sent by others that the recipient hasn't read yet.
   * A message is considered "unread" by the recipient if it doesn't have an associated 'READ' event.
   *
   * @param connectionId The ID of the connection.
   * @param userId The ID of the user who is reading the messages.
   * @return A list of messages to be marked as read.
   */
  @Query("""
      SELECT m
      FROM UserMessage m
      WHERE m.connection.id = :connectionId
        AND m.sender.id <> :userId
        AND NOT EXISTS (
          SELECT 1
          FROM MessageEvent ev
          WHERE ev.message = m
            AND ev.messageEventType.name = 'READ'
        )
      """)
  List<UserMessage> findMessagesToMarkAsRead(@Param("connectionId") Long connectionId, @Param("userId") Long userId);
}
