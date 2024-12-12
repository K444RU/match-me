package com.matchme.srv.model.user.profile.user_attributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.matchme.srv.model.user.profile.ProfileChange;
import com.matchme.srv.model.user.profile.UserGenderType;
import com.matchme.srv.model.user.profile.UserProfile;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "user_attributes")
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

  private LocalDate birth_date;

  private List<Double> location = new ArrayList<>(); //Geohash of 6-7 length

  @OneToMany(mappedBy = "userAttributes", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<ProfileChange> attributeChangeLog;

}
