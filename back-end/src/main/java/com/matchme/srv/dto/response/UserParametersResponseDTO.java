package com.matchme.srv.dto.response;

import java.util.Set;

public record UserParametersResponseDTO(
  
  String email,

  String password,

  String number,

  String first_name,
  String last_name,
  String alias,
  Set<Long> hobbies,

  Long gender_self,

  String birth_date,

  String city,
  Double longitude,
  Double latitude,

  Long gender_other,

  Integer age_min,

  Integer age_max,

  Integer distance,

  Double probability_tolerance

  // TODO: All enum tables 

) {}
