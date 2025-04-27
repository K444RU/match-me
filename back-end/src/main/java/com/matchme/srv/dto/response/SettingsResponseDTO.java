package com.matchme.srv.dto.response;

import java.util.Set;
import com.matchme.srv.model.user.profile.UserGenderEnum;
import lombok.Builder;

@Builder
public record SettingsResponseDTO(
    String email,
    String number,
    // String password;
    String firstName,
    String lastName,
    String alias,
    String aboutMe,
    Set<Long> hobbies,
    UserGenderEnum genderSelf,
    String birthDate,
    String city,
    Double longitude,
    Double latitude,
    UserGenderEnum genderOther,
    Integer ageMin,
    Integer ageMax,
    Integer distance,
    Double probabilityTolerance,
    String profilePicture
) {}
