package com.matchme.srv.model.user.profile.user_score;

import java.util.Set;

import com.matchme.srv.model.connection.DatingPoolSyncListener;
import com.matchme.srv.model.user.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing a user's scoring metrics in the dating application.
 * This class maintains various scores that influence the matching algorithm.
 *
 * The scores are automatically synchronized with the dating pool through
 * the DatingPoolSyncListener when changes occur.
 */
@Entity
@EntityListeners(DatingPoolSyncListener.class)
@Table(name = "user_scores")
@Getter
@Setter
@NoArgsConstructor
public class UserScore {

    /** Default ELO score for new users */
    private static final int DEFAULT_SCORE = 1000;

    /** Default probability of matching based on vibe - not in use for MVP */
    private static final double DEFAULT_VIBE_PROBABILITY = 1.0;

    /** Default blind matching score - not in use for MVP */
    private static final int DEFAULT_BLIND = 1000;

    @Id
    private Long id;

    /**
     * The user associated with these scores.
     * Mapped bidirectionally with a one-to-one relationship.
     * Maps to the User ID to UserScore ID through a one-to-one relationship.
     */
    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private int currentScore = DEFAULT_SCORE;

    private double vibeProbability = DEFAULT_VIBE_PROBABILITY;

    private int currentBlind = DEFAULT_BLIND;

    /**
     * Collection of connection results associated with this user's scores.
     * Tracks the history of matches and their outcomes.
     */
    @OneToMany(mappedBy = "userScores", cascade = CascadeType.ALL)
    private Set<ConnectionResult> results;

    /**
     * Constructs a new UserScore for the specified user.
     * Initializes all scores to their default values.
     *
     * @param user The user to associate with these scores
     */
    public UserScore(User user) {
        this.user = user;
    }

}
