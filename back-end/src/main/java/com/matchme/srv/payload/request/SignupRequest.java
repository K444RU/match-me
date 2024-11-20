package com.matchme.srv.payload.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {

  @NotBlank(message = "Email cannot be empty")
  @Size(max = 320, message = "Email must be less than 320 characters")
  @Email(message = "Email must be valid")
  private String email;

  @NotBlank(message = "Phone number cannot be empty")
  @Size(max = 20)
  private String number;

  @NotBlank(message = "Password cannot be empty")
  @Size(min = 6, max = 40, message = "Password must be between 6 and 40 characters")
  private String password;
}
