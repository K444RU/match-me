package com.matchme.srv.dto.request;

import java.time.LocalDate;
import java.util.Set;
import com.matchme.srv.constraints.BirthDate;
import com.matchme.srv.validation.annotations.NotBlankIfPresent;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UserParametersRequestDTO(
  @NotBlank(message = "First name is required")
  @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
  @Pattern(regexp = "^[a-zA-Z\\s-']+$", message = "First name can only contain letters, spaces, hyphens and apostrophes")
  String first_name,

  @NotBlank(message = "Last name is required")
  @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
  @Pattern(regexp = "^[a-zA-Z\\s-']+$", message = "Last name can only contain letters, spaces, hyphens and apostrophes")
  String last_name,

  @NotBlankIfPresent(message = "Alias cannot be empty if provided")
  @Size(min = 2, max = 30, message = "Alias must be between 2 and 30 characters")
  @Pattern(regexp = "^[a-zA-Z0-9\\s-_]+$", message = "Alias can only contain letters, numbers, spaces, hyphens and underscores")
  String alias,

  @Size(max = 5, message = "Maximum 5 hobbies allowed")
  Set<Long> hobbies,

  @NotNull(message = "Self gender must be specified")
  @Min(value = 1, message = "Invalid gender value")
  Long gender_self,

  @NotNull(message = "Birth date must be specified")
  @BirthDate(message = "Birth date must be greater than or equal to 18 years old")
  @Past(message = "Birth date must be in the past")
  LocalDate birth_date,
  
  @NotBlank(message = "City is required")
  @Size(min = 2, max = 100, message = "City name must be between 2 and 100 characters")
  String city,

  @Min(value = -180, message = "Longitude must be greater than or equal to -180")
  @Max(value = 180, message = "Longitude must be less than or equal to 180")
  Double longitude,

  @Min(value = -90, message = "Latitude must be greater than or equal to -90")
  @Max(value = 90, message = "Latitude must be less than or equal to 90")
  Double latitude,

  @NotNull(message = "Preferred gender must be specified")
  @Min(value = 1, message = "Invalid gender value")
  Long gender_other,

  @NotNull(message = "Minimum age must be specified")
  @Min(value = 18, message = "Minimum age must be at least 18")
  @Max(value = 120, message = "Minimum age must be less than 120")
  Integer age_min,

  @NotNull(message = "Maximum age must be specified")
  @Min(value = 18, message = "Maximum age must be at least 18")
  @Max(value = 120, message = "Maximum age must be less than 120")
  Integer age_max,

  @NotNull(message = "Distance must be specified")
  @Min(value = 50, message = "Distance must be at least 50")
  @Max(value = 300, message = "Distance must be less than or equal to 300")
  Integer distance,

  @NotNull(message = "Probability tolerance must be specified")
  @Min(value = 0, message = "Probability tolerance must be greater than or equal to 0")
  @Max(value = 1, message = "Probability tolerance must be less than or equal to 1")
  Double probability_tolerance
) {}
