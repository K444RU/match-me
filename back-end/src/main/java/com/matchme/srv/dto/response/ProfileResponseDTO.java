package com.matchme.srv.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileResponseDTO {
  private String first_name;
  private String last_name;
  private String city;
}
