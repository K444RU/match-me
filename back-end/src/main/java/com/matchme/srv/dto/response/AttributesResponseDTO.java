package com.matchme.srv.dto.response;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.matchme.srv.model.user.profile.UserGenderEnum;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttributesResponseDTO {
  
  private String gender;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate birth_date;

  private List<Double> location;

  public AttributesResponseDTO() {}

  public AttributesResponseDTO(UserGenderEnum gender, LocalDate birth_date, List<Double> location) {
    this.gender = gender.name();
    this.birth_date = birth_date;
    this.location = location;
  }

}
