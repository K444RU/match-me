package com.matchme.srv.dto.request;

import com.matchme.srv.validation.annotations.NotBlankIfPresent;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

// Needs to be alone for direct email unique constraint validation! 
record UserSettingsRequestDTO(
  @NotBlankIfPresent
  @Email(message = "The provided email is invalid.")
  String email, 
  
  @NotBlankIfPresent(message = "Your phone number cannot be empty")
  @Size(max = 20)
  String number, 
  
  @NotBlankIfPresent(message = "Your password cannot be empty")
  @Size(min = 6, max = 40, message = "Password must be between 6 and 40 characters.")
  String password, 
  
  boolean dark
  ) {}
