package com.matchme.srv.tracking.service;

import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.matchme.srv.tracking.ChangeListener;
import com.matchme.srv.tracking.model.ChangeLog;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j // Lombok annotation that creates a logger
public class AsyncChangeProcessor {
  private final List<ChangeListener> changeListeners;

  @Async
  public void processChange(ChangeLog changeLog) {
    changeListeners.forEach(listener -> {
      try {
        listener.onChange(changeLog);
      } catch (Exception e) {
        log.error("Error processing change with listener: {}", listener.getClass().getSimpleName(), e);
      }
    });
  }
}
