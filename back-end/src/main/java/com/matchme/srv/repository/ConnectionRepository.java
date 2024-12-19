package com.matchme.srv.repository;

import com.matchme.srv.model.connection.Connection;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConnectionRepository extends JpaRepository<Connection, Long> {

    @Query("SELECT c FROM Connection c JOIN c.users u WHERE u.id = :userId")
    List<Connection> findConnectionsByUserId(Long userId);

}
