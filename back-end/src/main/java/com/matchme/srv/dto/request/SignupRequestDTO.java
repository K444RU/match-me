package com.matchme.srv.dto.request;

import com.matchme.srv.validation.annotations.ValidPhoneNumber;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDTO {

  @NotBlank(message = "Email cannot be empty")
  @Size(max = 320, message = "Email must be less than 320 characters")
  @Email(message = "Email must be valid")
  private String email;

  @NotBlank(message = "Phone number cannot be empty")
  @Size(max = 20)
  @ValidPhoneNumber
  private String number;

  @NotBlank(message = "Password cannot be empty")
  @Size(min = 6, max = 40, message = "Password must be between 6 and 40 characters")
  private String password;
}
