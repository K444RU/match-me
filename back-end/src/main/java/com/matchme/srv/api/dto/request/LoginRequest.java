package com.matchme.srv.api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
  
  @NotBlank(message = "Email cannot be empty")
  @Size(max = 320, message = "Email must be less than 320 characters")
  @Email(message = "Email must be valid")
  private String email;

  @NotBlank(message = "Password cannot be empty")
  @Size(min = 6, max = 40, message = "Password must be between 6 and 40 characters")
  private String password;
}