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
            SELECT c FROM Connection c 
            JOIN c.users u1 
            JOIN c.users u2 
            WHERE u1.id = :userId1 
            AND u2.id = :userId2""")
    Connection findConnectionBetween(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    @Query("""
            SELECT CASE WHEN COUNT(c) > 0 THEN TRUE ELSE FALSE END
            FROM Connection c
            JOIN c.users u1
            JOIN c.users u2
            WHERE u1.id = :userId1 AND u2.id = :userId2
            AND EXISTS (SELECT cs FROM c.connectionStates cs WHERE cs.status = 'ACCEPTED')
            """)
    boolean existsConnectionBetween(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    @Query("""
            SELECT DISTINCT c
            FROM Connection c
            LEFT JOIN FETCH c.userMessages m
            JOIN c.users u
            WHERE u.id = :userId
            """)
    List<Connection> findConnectionsByUserIdWithMessages(@Param("userId") Long userId);

    @Query("SELECT DISTINCT c FROM Connection c " +
            "JOIN FETCH c.connectionStates " +
            "JOIN FETCH c.users " +
            "WHERE c.id IN (SELECT c2.id FROM Connection c2 JOIN c2.users u WHERE u.id = :userId)")
    List<Connection> findConnectionsByUserId(@Param("userId") Long userId);

    @Query("""
            SELECT CASE WHEN COUNT(cs) > 0 THEN TRUE ELSE FALSE END
            FROM Connection c
            JOIN c.connectionStates cs
            WHERE cs.requesterId = :requesterId AND cs.targetId = :targetId AND cs.status = 'PENDING'
            """)
    boolean hasPendingConnectionRequest(@Param("requesterId") Long requesterId, @Param("targetId") Long targetId);

    // Placeholder for recommendations (to be implemented later)
    default boolean isInRecommendations(Long currentUserId, Long targetUserId) {
        // TODO: Integrate with RecommendationService once implemented
        return false; // Stub for now
    }
}