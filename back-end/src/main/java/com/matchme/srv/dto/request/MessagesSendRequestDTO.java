package com.matchme.srv.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessagesSendRequestDTO {
    private Long connectionId;
    private String content;
}
