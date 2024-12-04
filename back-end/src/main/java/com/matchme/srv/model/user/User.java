package com.matchme.srv.model.user;

import java.util.HashSet;
import java.util.Set;

import com.matchme.srv.model.user.activity.ActivityLog;
import com.matchme.srv.model.user.profile.UserProfile;
import com.matchme.srv.model.user.profile.user_score.UserScore;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import lombok.Data;
import lombok.ToString;

@Data
@Entity
@Table(name = "users")
@ToString(exclude = "userAuth")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 50)
    @Email
    @Column(unique = true)
    private String email;

    //@NotBlank
    @Size(max = 20)
    private String number;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private UserAuth userAuth;

    // @NotBlank
    // @Size(max = 120)
    // private String password;

    @Enumerated(EnumType.STRING)
    private UserState state;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ActivityLog> activity;

    // CascadeType.ALL = when a user is deleted, the associated user profile is also deleted.
    // orphanRemoval = true -> profile is deleted when user is deleted. 
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    // @NotNull
    private UserProfile profile;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private UserScore score;

    public User() {}

    public User(String email) {
        this.email = email;
        this.state = UserState.UNVERIFIED;
        //this.roles.add();
    }

    // Persistence managers
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

    // Without helper method - inconsistent relationship
    // User user = new User();
    // UserProfile profile = new UserProfile();
    // user.profile = profile; profile.user is still null! Bad state.

    // With helper method - maintains both sides
    // User user = new User();
    // UserProfile profile = new UserProfile();
    // user.setProfile(profile); Sets both user.profile and profile.user
}
