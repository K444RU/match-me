package com.matchme.srv.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MarkReadRequestDTO {
    private Long connectionId;
}
