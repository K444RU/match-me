package com.matchme.srv.dto.response;

import java.util.Set;
import com.matchme.srv.model.user.profile.UserGenderEnum;
import lombok.Builder;
import lombok.Data;

/**
 * @return The users biographical data (the data used to power recommendations)
 */
@Data
@Builder
public class BiographicalResponseDTO {
    private UserGenderEnum gender_self;
    private UserGenderEnum gender_other;
    private Set<Long> hobbies;
    private Integer age_self;
    private Integer age_min;
    private Integer age_max;
    private Integer distance;
    private Double probability_tolerance;
}
