package com.matchme.srv.model.user;

import com.matchme.srv.model.user.activity.ActivityLog;
import com.matchme.srv.model.user.profile.UserProfile;
import com.matchme.srv.model.user.profile.user_score.UserScore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "users")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @NotBlank
    @Size(max = 50)
    @Email
    @Column(unique = true)
    @ToString.Include
    private String email;

    @Size(max = 20)
    private String number;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private UserAuth userAuth;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_state_type_id")
    private UserStateTypes state;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<UserRoleType> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ActivityLog> activity;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private UserProfile profile;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private UserScore score;

    public User() {}

    public User(String email, UserStateTypes state) {
        this.email = email;
        this.state = state;
    }

    public User(String email, String number, UserStateTypes state) {
        this.email = email;
        this.number = number;
        this.state = state;
    }

    // Helper methods to maintain bidirectional consistency:
    public void setProfile(UserProfile profile) {
        if (profile != null) {
            profile.setUser(this);
        }
        this.profile = profile;
    }

    public void setScore(UserScore score) {
        if (score != null) {
            score.setUser(this);
        }
        this.score = score;
    }

    public void setUserAuth(UserAuth userAuth) {
        if (userAuth != null) {
            userAuth.setUser(this);
        }
        this.userAuth = userAuth;
    }

    public void setRole(UserRoleType role) {
        this.roles.add(role);
    }
}
