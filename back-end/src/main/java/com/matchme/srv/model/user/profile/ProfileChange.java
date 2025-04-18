package com.matchme.srv.model.user.profile;

import java.time.Instant;

import com.matchme.srv.model.user.profile.user_attributes.UserAttributes;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "profile_changes")
public class ProfileChange {
  
  @Id
  @GeneratedValue( strategy = GenerationType.IDENTITY) 
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @NotNull
  @JoinColumn(name = "user_profile_id")
  private UserProfile userProfile;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "profile_change_type_id")
  private ProfileChangeType type;

  @NotNull
  private Instant instant;

  private String content; 

  @ManyToOne
  @JoinColumn(name = "user_attributes_id")
  private UserAttributes userAttributes;


  public ProfileChange() {}

  public ProfileChange(UserProfile userProfile, ProfileChangeType type, String content) {
    this.userProfile = userProfile;
    this.type = type;
    this.content = content;
    this.instant = Instant.now();
  }


}
