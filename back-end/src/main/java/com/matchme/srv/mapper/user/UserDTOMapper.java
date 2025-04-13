package com.matchme.srv.mapper.user;

import com.matchme.srv.dto.response.BiographicalResponseDTO;
import com.matchme.srv.dto.response.CurrentUserResponseDTO;
import com.matchme.srv.dto.response.GenderTypeDTO;
import com.matchme.srv.dto.response.ProfileResponseDTO;
import com.matchme.srv.dto.response.SettingsResponseDTO;
import com.matchme.srv.dto.response.UserParametersResponseDTO;
import com.matchme.srv.model.user.User;
import com.matchme.srv.model.user.profile.Hobby;
import com.matchme.srv.model.user.profile.UserProfile;

import java.time.LocalDate;
import java.time.Period;
import java.util.Base64;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserDTOMapper {
    public CurrentUserResponseDTO toCurrentUserResponseDTO(User user, boolean isOwner) {
        UserProfile profile = user.getProfile();

        String base64Picture = null;
        if (profile != null && profile.getProfilePicture() != null && profile.getProfilePicture().length > 0) {
            base64Picture = "data:image/png;base64," + Base64.getEncoder().encodeToString(profile.getProfilePicture());
        }

        return CurrentUserResponseDTO.builder()
                .id(user.getId())
                .email(isOwner ? user.getEmail() : null)
                .firstName(profile != null ? profile.getFirst_name() : null)
                .lastName(profile != null ? profile.getLast_name() : null)
                .alias(profile != null ? profile.getAlias() : null)
                .profilePicture(base64Picture)
                .role(user.getRoles())
                .profileLink("/api/users/" + user.getId() + "/profile")
                .state(user.getState())
                .build();
    }

    public SettingsResponseDTO toSettingsResponseDTO(UserParametersResponseDTO parameters) {
        String base64Picture = null;
        if (parameters.profilePicture() != null && parameters.profilePicture().length > 0) {
            base64Picture = "data:image/png;base64," + Base64.getEncoder().encodeToString(parameters.profilePicture());
        }

        return SettingsResponseDTO.builder()
                .email(parameters.email())
                .number(parameters.number())
                .firstName(parameters.first_name())
                .lastName(parameters.last_name())
                .alias(parameters.alias())
                .hobbies(parameters.hobbies())
                .genderSelf(parameters.gender_self())
                .birthDate(parameters.birth_date())
                .city(parameters.city())
                .longitude(parameters.longitude())
                .latitude(parameters.latitude())
                .genderOther(parameters.gender_other())
                .ageMin(parameters.age_min())
                .ageMax(parameters.age_max())
                .distance(parameters.distance())
                .probabilityTolerance(parameters.probability_tolerance())
                .profilePicture(base64Picture)
                .build();
    }

    public BiographicalResponseDTO tobBiographicalResponseDTO(UserProfile profile) {
        return BiographicalResponseDTO.builder()
                .gender_self(
                        new GenderTypeDTO(
                                profile.getAttributes().getGender().getId(),
                                profile.getAttributes().getGender().getName()))
                .gender_other(
                        new GenderTypeDTO(
                                profile.getPreferences().getGender().getId(),
                                profile.getPreferences().getGender().getName()))
                .hobbies(profile.getHobbies().stream().map(Hobby::getId).collect(Collectors.toSet()))
                .age_self(
                        Period.between(profile.getAttributes().getBirthdate(), LocalDate.now()).getYears())
                .age_min(profile.getPreferences().getAgeMin())
                .age_max(profile.getPreferences().getAgeMax())
                .distance(profile.getPreferences().getDistance())
                .probability_tolerance(profile.getPreferences().getProbabilityTolerance())
                .build();
    }

    public ProfileResponseDTO toProfileResponseDTO(UserProfile profile) {
        return ProfileResponseDTO.builder()
                .first_name(profile.getFirst_name())
                .last_name(profile.getLast_name())
                .city(profile.getCity())
                .build();
    }
}
