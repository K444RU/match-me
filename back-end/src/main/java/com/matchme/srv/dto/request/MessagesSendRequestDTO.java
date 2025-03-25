package com.matchme.srv.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessagesSendRequestDTO {
    @NotNull
    private Long connectionId;
    @NotNull
    private String content;
}
