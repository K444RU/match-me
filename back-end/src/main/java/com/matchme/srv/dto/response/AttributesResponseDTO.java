package com.matchme.srv.dto.response;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.matchme.srv.model.user.profile.Gender;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttributesResponseDTO {
  
  private String gender;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate birthDate;

  // TODO: Figure this out
  private List<Double> location;

  public AttributesResponseDTO() {}


  public AttributesResponseDTO(Gender gender, LocalDate birthDate, List<Double> location) {
    this.gender = gender.name();
    this.birthDate = birthDate;
    this.location = location;
  }

}
