package com.matchme.srv.model.user;

import java.security.SecureRandom;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString;

@Data
@Entity
@Table(name = "user_auth_data")
@ToString(exclude = "user")
public class UserAuth {
  
  @Id
  private Long id;

  @MapsId
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @NotBlank
  private String password; 

  private Integer recovery;

  // TODO: WTF, miks Lombok custom constructori lisades ei tööta? 
  public UserAuth() {}

  public UserAuth(String password) {
    this.password = password;

    SecureRandom secureRandom = new SecureRandom();
    this.recovery = 100000 + secureRandom.nextInt(900000);
  }

}
