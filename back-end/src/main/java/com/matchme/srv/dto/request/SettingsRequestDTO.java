package com.matchme.srv.dto.request;

import com.matchme.srv.constraints.BirthDate;
import com.matchme.srv.model.user.profile.UserGenderEnum;
import com.matchme.srv.validation.annotations.NotBlankIfPresent;
import com.matchme.srv.validation.annotations.ValidGender;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SettingsRequestDTO {

  // User Account
  @NotBlank(message = "Email is required")
  @Size(max = 320, message = "Email must be less than 320 characters")
  @Email(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Please provide a valid email address")
  @Pattern(regexp = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$", message = "Email format is invalid")
  private String email;

  @NotBlank(message = "Phone number is required")
  @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number must follow E.164 format")
  @Size(min = 8, max = 15, message = "Phone number must be between 8 and 15 digits")
  private String number;

  // User Profile
  @NotBlank(message = "First name is required")
  @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
  @Pattern(regexp = "^[a-zA-Z\\s-']+$", message = "First name can only contain letters, spaces, hyphens and apostrophes")
  private String first_name;

  @NotBlank(message = "Last name is required")
  @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
  @Pattern(regexp = "^[a-zA-Z\\s-']+$", message = "Last name can only contain letters, spaces, hyphens and apostrophes")
  private String last_name;

  @NotBlankIfPresent(message = "Alias cannot be empty if provided")
  @Size(min = 2, max = 30, message = "Alias must be between 2 and 30 characters")
  @Pattern(regexp = "^[a-zA-Z0-9\\s-_]+$", message = "Alias can only contain letters, numbers, spaces, hyphens and underscores")
  private String alias;
  
  @NotBlank(message = "City is required")
  @Size(min = 2, max = 100, message = "City name must be between 2 and 100 characters")
  private String city;

  // User Attributes
  @NotNull(message = "Self gender must be specified")
  @ValidGender(message = "Invalid gender value")
  private UserGenderEnum gender_self;

  @NotNull(message = "Birth date must be specified")
  @BirthDate(message = "Birth date must be greater than or equal to 18 years old")
  @Past(message = "Birth date must be in the past")
  private String birth_date;

  @Min(value = -180, message = "Longitude must be greater than or equal to -180")
  @Max(value = 180, message = "Longitude must be less than or equal to 180")
  private Double longitude;

  @Min(value = -90, message = "Latitude must be greater than or equal to -90")
  @Max(value = 90, message = "Latitude must be less than or equal to 90")
  private Double latitude;

  // User Preferences
  @NotNull(message = "Preferred gender must be specified")
  @ValidGender(message = "Invalid gender value")
  private UserGenderEnum gender_other;

  @NotNull(message = "Minimum age must be specified")
  @Min(value = 18, message = "Minimum age must be at least 18")
  @Max(value = 120, message = "Minimum age must be less than 120")
  private Integer age_min;

  @NotNull(message = "Maximum age must be specified")
  @Min(value = 18, message = "Maximum age must be at least 18")
  @Max(value = 120, message = "Maximum age must be less than 120")
  private Integer age_max;

  @NotNull(message = "Distance must be specified")
  @Min(value = 50, message = "Distance must be at least 50")
  @Max(value = 300, message = "Distance must be less than or equal to 300")
  private Integer distance;

  @NotNull(message = "Probability tolerance must be specified")
  @Min(value = 0, message = "Probability tolerance must be greater than or equal to 0")
  @Max(value = 1, message = "Probability tolerance must be less than or equal to 1")
  private Double probability_tolerance;

}
