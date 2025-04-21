package com.matchme.srv.dto.response;

import java.util.Set;

import com.matchme.srv.model.user.profile.UserGenderEnum;

public record UserParametersResponseDTO(
        String email,
        String password,
        String number,
        String first_name,
        String last_name,
        String alias,
        Set<Long> hobbies,
        UserGenderEnum gender_self,
        String birth_date,
        String city,
        Double longitude,
        Double latitude,
        UserGenderEnum gender_other,
        Integer age_min,
        Integer age_max,
        Integer distance,
        Double probability_tolerance,
        byte[] profilePicture
        // TODO: All enum tables
) {}
