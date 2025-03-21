package com.matchme.srv.model.connection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionSynchronization;

import com.matchme.srv.model.user.profile.user_preferences.UserPreferences;
import com.matchme.srv.util.SpringContext;

import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserPreferencesListener {

  private DatingPoolSyncService synchronizer;

  @Autowired
  public void setSynchronizer(DatingPoolSyncService synchronizer) {
    this.synchronizer = synchronizer;
  }

  /**
   * Handles updates to UserPreferences entities.
   * Triggered after an update to user preferences such as desired gender, age
   * range, or distance.
   *
   * @param preferences The updated UserPreferences entity
   */
  @PostUpdate
  public void onUserPreferencesChange(final UserPreferences preferences) {
    schedulePreferencesSync(preferences.getId());
    log.debug("UserPreferences change detected for ID: {}", preferences.getId());
  }

  /**
   * Schedules a preferences-specific synchronization of the dating pool entry.
   * Separate function because preferences can change more often (e.g. no
   * matches).
   * The synchronization is scheduled to occur after the current transaction
   * commits.
   *
   * @param profileId The ID of the profile whose preferences need to be
   *                  synchronized
   */
  private void schedulePreferencesSync(final Long profileId) {
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
