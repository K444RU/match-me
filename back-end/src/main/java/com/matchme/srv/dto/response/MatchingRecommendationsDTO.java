package com.matchme.srv.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class MatchingRecommendationsDTO {
  private List<Long> recommendations;
}