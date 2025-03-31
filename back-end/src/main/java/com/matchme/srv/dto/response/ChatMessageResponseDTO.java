package com.matchme.srv.dto.response;

import jakarta.validation.constraints.NotNull;
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
    @NotNull private Long messageId;
    @NotNull private Long connectionId;
    @NotNull private Long senderId;
    @NotNull private String senderAlias;
    @NotNull private String content;
    @NotNull private Instant createdAt;
    //toDo: we might add MessageEventType here as well?
}

