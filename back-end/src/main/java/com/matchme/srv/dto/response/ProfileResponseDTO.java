package com.matchme.srv.dto.response;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileResponseDTO {
  private String first_name;
  private String last_name;
  // TODO: add fields when they become apparent

  public ProfileResponseDTO() {}

  public ProfileResponseDTO(String first_name, String last_name) {
    this.first_name = first_name;
    this.last_name = last_name;
  }


}
