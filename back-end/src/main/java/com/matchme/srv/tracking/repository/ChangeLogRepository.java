package com.matchme.srv.tracking.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.matchme.srv.tracking.model.ChangeLog;
import com.matchme.srv.tracking.model.EntityType;

@Repository
public interface ChangeLogRepository extends JpaRepository<ChangeLog, Long> {
  List<ChangeLog> findByEntityTypeAndEntityId(EntityType entityType, Long entityId);
  List<ChangeLog> findByUserId(Long userId);

  @Query("SELECT cl FROM ChangeLog cl WHERE cl.entityType = :entityType " +
           "AND cl.entityId = :entityId ORDER BY cl.timestamp DESC")
    List<ChangeLog> findLatestChanges(
        @Param("entityType") EntityType entityType,
        @Param("entityId") Long entityId,
        Pageable pageable
    );
}
