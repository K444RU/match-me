package com.matchme.srv.repository;

import com.matchme.srv.model.connection.Connection;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConnectionRepository extends JpaRepository<Connection, Long> {

    @Query("""
    SELECT CASE WHEN COUNT(c) > 0 THEN TRUE ELSE FALSE END
    FROM Connection c
    JOIN c.users u1
    JOIN c.users u2
    WHERE u1.id = :userId1 AND u2.id = :userId2
    """)
    boolean existsConnectionBetween(Long userId1, Long userId2);

    @Query("""
    SELECT DISTINCT c
    FROM Connection c
    LEFT JOIN FETCH c.userMessages m
    JOIN c.users u
    WHERE u.id = :userId
    """)
    List<Connection> findConnectionsByUserIdWithMessages(@Param("userId") Long userId);

    @Query("""
    SELECT c 
    FROM Connection c 
    JOIN c.users u 
    WHERE u.id = :userId
    """)
    List<Connection> findConnectionsByUserId(Long userId);

    /*TODO:
    Create a @Query that fetches database result for existing pending connection request
    @Query("SELECT COUNT(r) > 0 FROM ConnectionRequest r WHERE " +
       "((r.sender.id = :userId1 AND r.receiver.id = :userId2) OR " +
       "(r.sender.id = :userId2 AND r.receiver.id = :userId1)) AND " +
       "r.status = 'PENDING'")
    */
//    boolean hasConnectionRequest(Long currentUserId, Long targetUserId);
//
//    boolean isInRecommendations(Long currentUserId, Long targetUserId);
}
