package com.matchme.srv.repository;

import com.matchme.srv.model.connection.Connection;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConnectionRepository extends JpaRepository<Connection, Long> {

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN TRUE ELSE FALSE END " + "FROM Connection c "
            + "JOIN c.users u1 " + "JOIN c.users u2 "
            + "WHERE u1.id = :userId1 AND u2.id = :userId2")
    boolean existsConnectionBetween(Long userId1, Long userId2);

    @Query("SELECT c FROM Connection c JOIN c.users u WHERE u.id = :userId")
    List<Connection> findConnectionsByUserId(Long userId);

}
