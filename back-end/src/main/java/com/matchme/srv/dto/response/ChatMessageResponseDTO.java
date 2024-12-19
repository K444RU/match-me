package com.matchme.srv.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageResponseDTO {
    private Long messageId;
    private Long connectionId;
    private String senderAlias;
    private String content;
    private Timestamp createdAt;
    //toDo: we might add MessageEventType here as well?
}

