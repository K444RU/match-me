package com.matchme.srv.model.connection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionSynchronization;

import com.matchme.srv.model.user.profile.UserProfile;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserProfileListener {

  private static DatingPoolSyncService synchronizer;

  @Autowired
  public void setSynchronizer(DatingPoolSyncService synchronizer) {
    UserProfileListener.synchronizer = synchronizer;
  }

  /**
   * Handles updates and creation of UserProfile entities.
   * Triggered after an update to or creation of a user profile.
   *
   * @param userProfile The updated or created UserProfile entity
   */
  @PostUpdate
  @PostPersist
  public void onUserProfileChange(final UserProfile userProfile) {
    scheduleSync(userProfile.getId());
    log.debug("UserProfile change detected for ID: {}", userProfile.getId());
  }

  /**
   * Schedules a full synchronization of the dating pool entry for a given profile
   * ID.
   * The synchronization is scheduled to occur after the current transaction
   * commits.
   *
   * @param profileId The ID of the profile to be synchronized
   */
  private void scheduleSync(final Long profileId) {
    if (TransactionSynchronizationManager.isActualTransactionActive()) {
      TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
        @Override
        public void afterCommit() {
          synchronizer.synchronizeDatingPool(profileId);
        }
      });
    }
  }
}
