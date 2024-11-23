package com.matchme.srv.user.user_profile;

import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "user_attributes")
public class UserAttributes {
  
  @Id // need to look into shared id value with user
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  private Gender gender;

  @NotNull
  private Integer age;

  @NotNull
  private String location; // Need to look into location representation in database

  @NotNull
  private Integer score; // Maybe not the best place to put it and should be calculated when adding to pool...

  @OneToMany(mappedBy = "", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<ProfileChange> attributeChangeLog;

}
