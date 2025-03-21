package com.matchme.srv.model.connection;

import com.matchme.srv.model.user.profile.user_attributes.UserAttributes;
import com.matchme.srv.util.SpringContext;
import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
public class UserAttributesListener {

  /**
   * Handles updates to UserAttributes entities. Triggered after an update to user attributes such
   * as gender, birthdate, or location.
   *
   * @param attributes The updated UserAttributes entity
   */
  @PostUpdate
  public void onUserAttributesChange(final UserAttributes attributes) {
    scheduleSync(attributes.getId());
    log.debug("UserAttributes change detected for ID: {}", attributes.getId());
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
