package com.matchme.srv.dto.response;

public record UserParametersResponseDTO(
  
  String email,

  String password,

  String number,

  Integer gender_self,

  String birthDate,

  Double longitude,
  Double latitude,

  Integer gender_other,

  Integer age_min,

  Integer age_max,

  Integer distance,

  Double probabilityTolerance

  // TODO: All enum tables 

) {}
