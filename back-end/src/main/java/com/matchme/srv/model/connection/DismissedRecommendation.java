package com.matchme.srv.model.connection;

import com.matchme.srv.model.user.profile.UserProfile;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "dismissed_recommendations")
@Data
public class DismissedRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_profile_id", nullable = false)
    private UserProfile userProfile;

    @ManyToOne
    @JoinColumn(name = "dismissed_user_profile_id", nullable = false)
    private UserProfile dismissedUserProfile;
}
