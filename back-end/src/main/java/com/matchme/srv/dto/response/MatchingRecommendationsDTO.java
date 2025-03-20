package com.matchme.srv.dto.response;

import java.util.List;
import java.util.Set;

import lombok.Data;

@Data
public class MatchingRecommendationsDTO {
  private List<RecommendedUserDTO> recommendations;

  @Data
  public static class RecommendedUserDTO {
    private Long userId;
    private String firstName;
    private String lastName;
    private String profilePicture;
    private Integer age;
    private String gender;
    private Integer distance;
    private Set<String> hobbies;
    private Double probability;
  }
}
