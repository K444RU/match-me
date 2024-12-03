package com.matchme.srv.dto.response;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileResponseDTO {
  private String firstName;
  private String lastName;
  // TODO: add fields when they become apparent

  public ProfileResponseDTO() {}

  public ProfileResponseDTO(String firstName, String lastName) {
    this.firstName = firstName;
    this.lastName = lastName;
  }


}
