package com.matchme.srv.model.connection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionSynchronization;

import com.matchme.srv.model.user.profile.user_attributes.UserAttributes;
import com.matchme.srv.model.user.profile.user_preferences.UserPreferences;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DatingPoolSyncListener {

    private static DatingPoolSyncService synchronizer;

    @Autowired
    public void setSynchronizer(DatingPoolSyncService synchronizer) {
        DatingPoolSyncListener.synchronizer = synchronizer;
    }

    @PostUpdate
    @PostPersist
    public void onUserAttributesChange(final UserAttributes attributes) {
        scheduleSync(attributes.getId());
        log.debug("UserAttributes change detected for ID: {}", attributes.getId());
    }

    @PostUpdate
    @PostPersist
    public void onUserPreferencesChange(final UserPreferences preferences) {
        scheduleSync(preferences.getId());
        log.debug("UserPreferences change detected for ID: {}", preferences.getId());
    }

    private void scheduleSync(final Long profileId) {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    synchronizer.synchronizeProfile(profileId);
                }
            });
        }
    }
}
