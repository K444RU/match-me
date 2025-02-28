package com.matchme.srv.model.user.profile.user_attributes;

import com.matchme.srv.model.connection.DatingPoolSyncListener;
import com.matchme.srv.model.user.profile.ProfileChange;
import com.matchme.srv.model.user.profile.UserGenderType;
import com.matchme.srv.model.user.profile.UserProfile;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.Data;
import lombok.ToString;

@Data
@Entity
@EntityListeners(DatingPoolSyncListener.class)
@Table(name = "user_attributes")
@ToString(exclude = "userProfile")
public class UserAttributes {

    @Id
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserProfile userProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gender_id")
    private UserGenderType gender;

    private LocalDate birthdate;

    private List<Double> location = new ArrayList<>(); // coordinates

    @OneToMany(mappedBy = "userAttributes", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ProfileChange> attributeChangeLog;
}
