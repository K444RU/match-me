package com.matchme.srv.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.Set;

@Data
public class RecommendedUserDTO {
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

    // Graphql fix
    public String getId() {
        return userId != null ? String.valueOf(userId) : null;
    }
}