package com.matchme.srv.model.user.profile.user_attributes;

import java.time.Instant;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "user_attribute_changes")
public class AttributeChange {
  
  @Id
  @GeneratedValue( strategy = GenerationType.IDENTITY) 
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @NotNull
  @JoinColumn(name = "user_attributes_id")
  private UserAttributes userAttributes;

  @Enumerated(EnumType.STRING)
  private AttributeChangeType type;

  @NotNull
  private Instant instant;

  private String content; 

  public enum AttributeChangeType {
    CREATED, GENDER, BIRTHDATE, LOCATION
  }

  public AttributeChange() {}

  public AttributeChange(UserAttributes attributes, AttributeChangeType type, String content) {
    this.userAttributes = attributes;
    this.type = type;
    this.content = content;
    this.instant = Instant.now();
  }
}
