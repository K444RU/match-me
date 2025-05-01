package com.matchme.srv.dto.graphql;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PongDTO {
  private String timestamp;
  private String status;
  private Long userId;
}
