package com.matchme.srv.dto.request.settings;

import com.matchme.srv.model.user.profile.UserGenderEnum;
import com.matchme.srv.validation.annotations.ValidGender;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreferencesSettingsRequestDTO {
    @NotNull(message = "Preferred gender must be specified")
    @ValidGender(message = "Invalid gender value")
    private UserGenderEnum gender_other;

    @NotNull(message = "Minimum age must be specified")
    @Min(value = 18, message = "Minimum age must be at least 18")
    @Max(value = 120, message = "Minimum age must be less than 120")
    private Integer age_min;

    @NotNull(message = "Maximum age must be specified")
    @Min(value = 18, message = "Maximum age must be at least 18")
    @Max(value = 120, message = "Maximum age must be less than 120")
    private Integer age_max;

    @NotNull(message = "Distance must be specified")
    @Min(value = 50, message = "Distance must be at least 50")
    @Max(value = 300, message = "Distance must be less than or equal to 300")
    private Integer distance;

    @NotNull(message = "Probability tolerance must be specified")
    @Min(value = 0, message = "Probability tolerance must be greater than or equal to 0")
    @Max(value = 1, message = "Probability tolerance must be less than or equal to 1")
    private Double probability_tolerance;
}
