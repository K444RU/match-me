package com.matchme.srv.dto.response;

public record UserParametersResponseDTO(
  
  String email,

  String password,

  String number,

  String firstName,
  String lastName,
  String alias,

  Long gender_self,

  String birthDate,

  Double longitude,
  Double latitude,

  Long gender_other,

  Integer age_min,

  Integer age_max,

  Integer distance,

  Double probabilityTolerance

  // TODO: All enum tables 

) {}
