package com.matchme.srv.dto.response;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import lombok.Data;

@Data
public class MatchingRecommendationsDTO {
  private List<RecommendedUserDTO> recommendations;

  @Data
  public static class RecommendedUserDTO {
    @NotNull private Long userId;
    @NotNull private String firstName;
    @NotNull private String lastName;
    private String profilePicture;
    @NotNull private Integer age;
    @NotNull private String gender;
    @NotNull private Integer distance;
    private Set<String> hobbies;
    @NotNull private Double probability;
    private String connectionStatus;
    private Long connectionId;
  }
}
