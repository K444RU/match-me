package com.matchme.srv.dto.request.settings;

import java.time.LocalDate;

import com.matchme.srv.constraints.BirthDate;
import com.matchme.srv.model.user.profile.UserGenderEnum;
import com.matchme.srv.validation.annotations.ValidGender;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
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
public class AttributesSettingsRequestDTO {
    @NotNull(message = "Self gender must be specified")
    @ValidGender(message = "Invalid gender value")
    private UserGenderEnum gender_self;

    @NotNull(message = "Birth date must be specified")
    @BirthDate(message = "Birth date must be greater than or equal to 18 years old")
    @Past(message = "Birth date must be in the past")
    private LocalDate birth_date;

    @NotBlank(message = "City is required")
    @Size(min = 2, max = 100, message = "City name must be between 2 and 100 characters")
    private String city;

    @Min(value = -180, message = "Longitude must be greater than or equal to -180")
    @Max(value = 180, message = "Longitude must be less than or equal to 180")
    private Double longitude;

    @Min(value = -90, message = "Latitude must be greater than or equal to -90")
    @Max(value = 90, message = "Latitude must be less than or equal to 90")
    private Double latitude;
}