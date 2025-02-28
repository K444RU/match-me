package com.matchme.srv.model.connection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionSynchronization;

import com.matchme.srv.model.user.profile.UserProfile;
import com.matchme.srv.model.user.profile.user_attributes.UserAttributes;
import com.matchme.srv.model.user.profile.user_preferences.UserPreferences;
import com.matchme.srv.model.user.profile.user_score.UserScore;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;

/**
 * Entity listener that synchronizes changes in user-related entities with the
 * dating pool.
 * This listener monitors updates to UserAttributes, UserPreferences, UserScore,
 * and UserProfile
 * entities and ensures the dating pool is kept in sync with these changes.
 * 
 * The synchronization is scheduled to occur after the current transaction
 * commits to ensure
 * data consistency and avoid potential race conditions.
 */
@Component
@Slf4j
public class DatingPoolSyncListener {

    private static DatingPoolSyncService synchronizer;

    @Autowired
    public void setSynchronizer(DatingPoolSyncService synchronizer) {
        DatingPoolSyncListener.synchronizer = synchronizer;
    }

    /**
     * Handles updates to UserAttributes entities.
     * Triggered after an update to user attributes such as gender, birthdate, or
     * location.
     *
     * @param attributes The updated UserAttributes entity
     */
    @PostUpdate
    public void onUserAttributesChange(final UserAttributes attributes) {
        scheduleSync(attributes.getId());
        log.debug("UserAttributes change detected for ID: {}", attributes.getId());
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
     * Handles updates and creation of UserProfile entities.
     * Triggered after an update to or creation of a user profile.
     *
     * @param userProfile The updated or created UserProfile entity
     */
    @PostUpdate
    @PostPersist
    public void onUserProfileChange(final UserProfile userProfile) {
        scheduleSync(userProfile.getId());
        log.debug("UserScore change detected for ID: {}", userProfile.getId());
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
                    synchronizer.synchronizeUserScore(profileId);
                }
            });
        }
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
                    synchronizer.synchronizeUserPreferences(profileId);
                }
            });
        }
    }
}
