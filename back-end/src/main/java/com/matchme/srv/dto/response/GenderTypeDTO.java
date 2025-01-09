package com.matchme.srv.dto.response;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@Builder
@AllArgsConstructor
public class GenderTypeDTO {
    @NotNull
    private Long id;
    @NotNull
    private String name;
}
