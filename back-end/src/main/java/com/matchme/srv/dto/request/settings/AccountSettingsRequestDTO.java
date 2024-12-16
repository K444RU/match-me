package com.matchme.srv.dto.request.settings;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountSettingsRequestDTO {
    @NotBlank(message = "Email is required")
    @Size(max = 320, message = "Email must be less than 320 characters")
    @Email(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Please provide a valid email address")
    @Pattern(regexp = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$", message = "Email format is invalid")
    private String email;

    @NotBlank(message = "Phone number is required")
    // TODO: add countrycode
    // @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number must follow E.164 format")
    // @Size(min = 8, max = 15, message = "Phone number must be between 8 and 15 digits")
    private String number;
}