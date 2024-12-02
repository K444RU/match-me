package com.matchme.srv.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.matchme.srv.model.user.activity.ActivityLog;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long>{
  
}
