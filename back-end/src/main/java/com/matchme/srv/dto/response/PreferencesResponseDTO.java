package com.matchme.srv.dto.response;

import com.matchme.srv.model.user.profile.Gender;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PreferencesResponseDTO {
  
  private String gender;
  private Integer age_min;
  private Integer age_max;
  private Integer distance;

  public PreferencesResponseDTO() {}

  public PreferencesResponseDTO(Gender gender, Integer min, Integer max, Integer distance) {
    this.gender = gender.getName();
    this.age_min = min;
    this.age_max = max;
    this.distance = distance;
  }
}
