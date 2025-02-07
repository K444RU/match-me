package com.matchme.srv.dto.request.settings;

import com.matchme.srv.validation.annotations.ValidPhoneNumber;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.RequiredArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class AccountSettingsRequestDTO {
    @NotBlank(message = "Email is required")
    @Size(max = 320, message = "Email must be less than 320 characters")
    @Email(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Please provide a valid email address")
    @Pattern(regexp = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$", message = "Email format is invalid")
    private String email;

    @NotBlank(message = "Phone number cannot be empty")
    @Size(max = 20)
    @ValidPhoneNumber
    private String number;
}