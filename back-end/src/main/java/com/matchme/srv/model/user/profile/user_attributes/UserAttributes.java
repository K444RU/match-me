package com.matchme.srv.model.user.profile.user_attributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import com.matchme.srv.model.user.profile.Gender;
import com.matchme.srv.model.user.profile.ProfileChange;
import com.matchme.srv.model.user.profile.UserProfile;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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

  @NotNull
  private Gender gender;

  @NotNull
  private LocalDate birthDate;

  @NotNull
  private List<Double> location; //Geohash of 6-7 length

  @OneToMany(mappedBy = "", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<ProfileChange> attributeChangeLog;

}
