package com.matchme.srv.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class BatchUserResponseDTO {
    private List<RecommendedUserDTO> users;
}
