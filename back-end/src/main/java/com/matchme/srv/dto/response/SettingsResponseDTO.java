package com.matchme.srv.dto.response;

import lombok.Builder;

@Builder
public record SettingsResponseDTO(
    String email,
    String number,
    // String password;
    String firstName,
    String lastName,
    String alias,
    Long genderSelf,
    String birthDate,
    String city,
    Double longitude,
    Double latitude,
    Long genderOther,
    Integer ageMin,
    Integer ageMax,
    Integer distance,
    Double probabilityTolerance

) {

}
