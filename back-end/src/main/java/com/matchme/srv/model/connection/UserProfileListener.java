package com.matchme.srv.model.connection;

import com.matchme.srv.model.user.profile.UserProfile;
import com.matchme.srv.util.SpringContext;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
public class UserProfileListener {

  /**
   * Handles updates and creation of UserProfile entities. Triggered after an update to or creation
   * of a user profile.
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
   * Schedules a full synchronization of the dating pool entry for a given profile ID. The
   * synchronization is scheduled to occur after the current transaction commits.
   *
   * @param profileId The ID of the profile to be synchronized
   */
  private void scheduleSync(final Long profileId) {
    if (TransactionSynchronizationManager.isActualTransactionActive()) {
      TransactionSynchronizationManager.registerSynchronization(
          new TransactionSynchronization() {
            @Override
            public void afterCommit() {
              SpringContext.getBean(DatingPoolSyncService.class).synchronizeDatingPool(profileId);
            }
          });
    }
  }
}
