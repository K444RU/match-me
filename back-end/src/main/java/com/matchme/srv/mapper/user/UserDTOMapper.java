package com.matchme.srv.mapper.user;

import com.matchme.srv.dto.response.BiographicalResponseDTO;
import com.matchme.srv.dto.response.CurrentUserResponseDTO;
import com.matchme.srv.dto.response.HobbyResponseDTO;
import com.matchme.srv.dto.response.ProfileResponseDTO;
import com.matchme.srv.dto.response.SettingsResponseDTO;
import com.matchme.srv.dto.response.UserParametersResponseDTO;
import com.matchme.srv.model.user.User;
import com.matchme.srv.model.user.profile.Hobby;
import com.matchme.srv.model.user.profile.UserProfile;

import java.time.LocalDate;
import java.time.Period;
import java.util.Base64;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import com.matchme.srv.util.ImageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserDTOMapper {
    public CurrentUserResponseDTO toCurrentUserResponseDTO(User user, boolean isOwner) {
        UserProfile profile = user.getProfile();
        String base64Picture = ImageUtils.toBase64Image(profile != null ? profile.getProfilePicture() : null);;

        Set<HobbyResponseDTO> hobbyDTOs = Collections.emptySet();
        if (profile != null && profile.getHobbies() != null) {
            hobbyDTOs = profile.getHobbies().stream()
                    .map(hobby -> HobbyResponseDTO.builder().id(hobby.getId()).name(hobby.getName()).build())
                    .collect(Collectors.toSet());
        }

        return CurrentUserResponseDTO.builder()
                .id(user.getId())
                .email(isOwner ? user.getEmail() : null)
                .firstName(profile != null ? profile.getFirst_name() : null)
                .lastName(profile != null ? profile.getLast_name() : null)
                .alias(profile != null ? profile.getAlias() : null)
                .aboutMe(profile != null ? profile.getAboutMe() : null)
                .hobbies(hobbyDTOs.isEmpty() ? null : hobbyDTOs)
                .profilePicture(base64Picture)
                .role(user.getRoles())
                .profileLink("/api/users/" + user.getId() + "/profile")
                .state(user.getState())
                .build();
    }

    public SettingsResponseDTO toSettingsResponseDTO(UserParametersResponseDTO parameters) {
        String base64Picture = ImageUtils.toBase64Image(parameters.profilePicture());

        return SettingsResponseDTO.builder()
                .email(parameters.email())
                .number(parameters.number())
                .firstName(parameters.first_name())
                .lastName(parameters.last_name())
                .alias(parameters.alias())
                .aboutMe(parameters.aboutMe())
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
                .gender_self(profile.getAttributes().getGender())
                .gender_other(profile.getPreferences().getGender())
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
        String base64Picture = ImageUtils.toBase64Image(profile.getProfilePicture());

        return ProfileResponseDTO.builder()
                .firstName(profile.getFirst_name())
                .lastName(profile.getLast_name())
                .city(profile.getCity())
                .hobbies(profile.getHobbies().stream().map(hobby -> HobbyResponseDTO.builder().id(hobby.getId()).name(hobby.getName()).build()).collect(Collectors.toSet()))
                .aboutMe(profile.getAboutMe())
                .profilePicture(base64Picture)
                .build();
    }
}
