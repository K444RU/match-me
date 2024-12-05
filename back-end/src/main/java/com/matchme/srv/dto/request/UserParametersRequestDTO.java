package com.matchme.srv.dto.request;


import com.matchme.srv.model.user.profile.Gender;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

public record UserParametersRequestDTO(

  Gender gender_self,

  @Pattern(regexp="^\\d{4}-\\d{2}-\\d{2}$", message = "Birthday must be in format (YYYY-MM-DD)")
  String birthDate,

  Double longitude,
  Double latitude,

  Gender gender_other,

  @Min(18)
  Integer age_min,

  @Max(120)
  Integer age_max,

  @Min(50)
  @Max(300)
  Integer distance,

  // Needs custom validation due to approx. errors
  Double probabilityTolerance
) {}
