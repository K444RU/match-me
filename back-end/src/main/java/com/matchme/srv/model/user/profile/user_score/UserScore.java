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
import lombok.Data;

@Data
@Entity
@EntityListeners(DatingPoolSyncListener.class)
@Table(name = "user_scores")
public class UserScore {

    @Id
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    // @NotNull
    private User user;

    private int currentScore; // ELO of yourself

    private double vibeProbability;

    private int currentBlind; // ELO of preference

    @OneToMany(mappedBy = "userScores", cascade = CascadeType.ALL)
    private Set<ConnectionResult> results;

    public UserScore() {
        this.currentScore = 1000;
        this.vibeProbability = 1;
        this.currentBlind = 1000;
    }

}
