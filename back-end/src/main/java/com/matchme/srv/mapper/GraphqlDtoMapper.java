package com.matchme.srv.mapper;

import com.matchme.srv.dto.graphql.UserInputs.AccountSettingsInput;
import com.matchme.srv.dto.graphql.UserInputs.AttributesSettingsInput;
import com.matchme.srv.dto.graphql.UserInputs.PreferencesSettingsInput;
import com.matchme.srv.dto.graphql.UserInputs.ProfilePictureInput;
import com.matchme.srv.dto.graphql.UserInputs.ProfileSettingsInput;
import com.matchme.srv.dto.graphql.UserInputs.UserParametersInput;
import com.matchme.srv.dto.request.UserParametersRequestDTO;
import com.matchme.srv.dto.request.settings.AccountSettingsRequestDTO;
import com.matchme.srv.dto.request.settings.AttributesSettingsRequestDTO;
import com.matchme.srv.dto.request.settings.PreferencesSettingsRequestDTO;
import com.matchme.srv.dto.request.settings.ProfilePictureSettingsRequestDTO;
import com.matchme.srv.dto.request.settings.ProfileSettingsRequestDTO;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class GraphqlDtoMapper {

  public UserParametersRequestDTO toUserParametersRequestDTO(UserParametersInput input) {
    return UserParametersRequestDTO.builder()
        .first_name(input.first_name())
        .last_name(input.last_name())
        .alias(input.alias())
        .aboutMe(input.aboutMe())
        .hobbies(mapHobbiesIds(input.hobbies()))
        .gender_self(input.gender_self())
        .birth_date(LocalDate.parse(input.birth_date()))
        .city(input.city())
        .longitude(input.longitude())
        .latitude(input.latitude())
        .gender_other(input.gender_other())
        .age_min(input.age_min())
        .age_max(input.age_max())
        .distance(input.distance())
        .probability_tolerance(input.probability_tolerance())
        .build();
  }

  public AccountSettingsRequestDTO toAccountSettingsRequestDTO(AccountSettingsInput input) {
    return AccountSettingsRequestDTO.builder().email(input.email()).number(input.number()).build();
  }

  public ProfileSettingsRequestDTO toProfileSettingsRequestDTO(ProfileSettingsInput input) {
    return ProfileSettingsRequestDTO.builder()
        .first_name(input.first_name())
        .last_name(input.last_name())
        .alias(input.alias())
        .aboutMe(input.aboutMe())
        .hobbies(mapHobbiesIds(input.hobbies()))
        .build();
  }

  public AttributesSettingsRequestDTO toAttributesSettingsRequestDTO(
      AttributesSettingsInput input) {
    return AttributesSettingsRequestDTO.builder()
        .gender_self(input.gender_self())
        .birth_date(LocalDate.parse(input.birth_date()))
        .city(input.city())
        .longitude(input.longitude())
        .latitude(input.latitude())
        .build();
  }

  public PreferencesSettingsRequestDTO toPreferencesSettingsRequestDTO(
      PreferencesSettingsInput input) {
    return PreferencesSettingsRequestDTO.builder()
        .gender_other(input.gender_other())
        .age_min(input.age_min())
        .age_max(input.age_max())
        .distance(input.distance())
        .probability_tolerance(input.probability_tolerance())
        .build();
  }

  public ProfilePictureSettingsRequestDTO toProfilePictureSettingsRequestDTO(
      ProfilePictureInput input) {
    return ProfilePictureSettingsRequestDTO.builder().base64Image(input.base64Image()).build();
  }

  private Set<Long> mapHobbiesIds(Set<String> hobbiesIds) {
    if (hobbiesIds == null) {
      return null;
    }
    return hobbiesIds.stream().map(Long::parseLong).collect(Collectors.toSet());
  }
}
