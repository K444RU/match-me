package com.matchme.srv.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SettingsRequestDTO {

  @Email(message = "The provided email is invalid.")
  private String email;

  @NotBlank(message = "Phone number cannot be empty")
  @Size(max = 20)
  private String number;

  @NotBlank(message = "Your password cannot be empty.")
  @Size(min = 6, max = 40, message = "Password must be between 6 and 40 characters")
  private String password;

  private boolean dark;

  SettingsRequestDTO() {}


}
