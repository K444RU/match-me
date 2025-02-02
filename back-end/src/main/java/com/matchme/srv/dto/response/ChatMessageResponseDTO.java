package com.matchme.srv.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageResponseDTO {
    private Long messageId;
    private Long connectionId;
    private String senderAlias;
    private String content;
    private Instant createdAt;
    //toDo: we might add MessageEventType here as well?
}

