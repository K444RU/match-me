package com.matchme.srv.model.connection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.matchme.srv.model.user.profile.user_score.UserScore;
import com.matchme.srv.util.SpringContext;
import jakarta.persistence.PostUpdate;

import org.springframework.transaction.support.TransactionSynchronization;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserScoreListener {

  private DatingPoolSyncService synchronizer;

  @Autowired
  public void setSynchronizer(DatingPoolSyncService synchronizer) {
    this.synchronizer = synchronizer;
  }

  /**
   * Handles updates to UserScore entities.
   * Triggered after an update to a user's score, which affects matching
   * algorithms.
   *
   * @param userScore The updated UserScore entity
   */
  @PostUpdate
  public void onUserScoreChange(final UserScore userScore) {
    scheduleScoreSync(userScore.getId());
    log.debug("UserScore change detected for ID: {}", userScore.getId());
  }

  /**
   * Schedules a score-specific synchronization of the dating pool entry.
   * Separate function because score changes often (e.g. after match/reject).
   * The synchronization is scheduled to occur after the current transaction
   * commits.
   *
   * @param profileId The ID of the profile whose score needs to be synchronized
   */
  private void scheduleScoreSync(final Long profileId) {
    if (TransactionSynchronizationManager.isActualTransactionActive()) {
      TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
        @Override
        public void afterCommit() {
          SpringContext.getBean(DatingPoolSyncService.class).synchronizeDatingPool(profileId);
        }
      });
    }
  }
}
