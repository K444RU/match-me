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
// import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@EntityListeners(DatingPoolSyncListener.class)
@Table(name = "user_scores")
@Getter
@Setter
@NoArgsConstructor
public class UserScore {

    private static final int DEFAULT_SCORE = 1000;
    private static final double DEFAULT_VIBE_PROBABILITY = 1.0;
    private static final int DEFAULT_BLIND = 1000;

    @Id
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    // @NotNull
    private User user;

    private int currentScore = DEFAULT_SCORE; // ELO of yourself

    private double vibeProbability = DEFAULT_VIBE_PROBABILITY;

    private int currentBlind = DEFAULT_BLIND; // ELO of preference

    @OneToMany(mappedBy = "userScores", cascade = CascadeType.ALL)
    private Set<ConnectionResult> results;

    public UserScore(User user) {
        this.user = user;
    }

}
