package com.matchme.srv.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HobbyResponseDTO {
    private Long id;
    private String name;
}