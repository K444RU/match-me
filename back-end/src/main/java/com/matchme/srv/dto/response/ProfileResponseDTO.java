package com.matchme.srv.dto.response;

import java.util.Set;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileResponseDTO {
  private String firstName;
  private String lastName;
  private String city;
  private Set<HobbyResponseDTO> hobbies;
}
