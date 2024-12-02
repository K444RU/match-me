package com.matchme.srv.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SettingsResponseDTO {
  private String email;
  private String number;
  private String password;


  public SettingsResponseDTO(String email, String number, String password) {
    this.email = email;
    this.number = number;
    this.password = password;
  }
}
