package com.matchme.srv.user.user_profile;

import java.sql.Timestamp;

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

  @Enumerated(EnumType.STRING)
  private ProfileChangeType type;

  @NotNull
  private Timestamp timestamp;

  @NotNull 
  private String newState; 

  public enum ProfileChangeType {
    AGE, BIO, PHOTO, INTERESTS // Each profile property should have it's own enum. Attributes too - because pertain changes about self.
  }
}
