package com.matchme.srv.dto.graphql;

import com.matchme.srv.model.user.profile.UserGenderEnum;
import java.util.Set;

public class UserInputs {
  public record UserParametersInput(
      String first_name,
      String last_name,
      String alias,
      String aboutMe,
      Set<String> hobbies,
      UserGenderEnum gender_self,
      String birth_date,
      String city,
      Double longitude,
      Double latitude,
      UserGenderEnum gender_other,
      Integer age_min,
      Integer age_max,
      Integer distance,
      Double probability_tolerance) {}

  public record ProfileSettingsInput(
      String first_name, String last_name, String alias, String aboutMe, Set<String> hobbies) {}

  public record AccountSettingsInput(String email, String number) {}

  public record AttributesSettingsInput(
      UserGenderEnum gender_self,
      String birth_date,
      String city,
      Double longitude,
      Double latitude) {}

  public record PreferencesSettingsInput(
      UserGenderEnum gender_other,
      Integer age_min,
      Integer age_max,
      Integer distance,
      Double probability_tolerance) {}

  public record ProfilePictureInput(String base64Image) {}
}
