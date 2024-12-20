package com.matchme.srv.dto.response;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@Builder
@AllArgsConstructor
public class GenderTypeDTO {
    private Long id;
    private String name;
}
