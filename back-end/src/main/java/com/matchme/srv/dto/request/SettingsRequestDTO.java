package com.matchme.srv.dto.request;


import com.matchme.srv.validation.annotations.NotBlankIfPresent;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SettingsRequestDTO {

  @NotBlankIfPresent
  @Email(message = "The provided email is invalid.")
  private String email;

  @NotBlankIfPresent(message = "Phone number cannot be empty")
  @Size(max = 20)
  private String number;

  @NotBlankIfPresent(message = "Your password cannot be empty.")
  @Size(min = 6, max = 40, message = "Password must be between 6 and 40 characters")
  private String password;

  private boolean dark;

}
