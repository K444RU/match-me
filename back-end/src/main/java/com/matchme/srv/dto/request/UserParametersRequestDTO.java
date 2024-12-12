package com.matchme.srv.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

public record UserParametersRequestDTO(

  String first_name,
  String last_name,
  String alias,

  Long gender_self,

  @Pattern(regexp="^\\d{4}-\\d{2}-\\d{2}$", message = "Birthday must be in format (YYYY-MM-DD)")
  String birth_date,

  String city,
  Double longitude,
  Double latitude,

  Long gender_other,

  @Min(18)
  Integer age_min,

  @Max(120)
  Integer age_max,

  @Min(50)
  @Max(300)
  Integer distance,

  // Needs custom validation due to approx. errors
  Double probability_tolerance
) {}
