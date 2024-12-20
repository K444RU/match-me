package com.matchme.srv.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatPreviewResponseDTO {
    private Long connectionId;
    private Long connectedUserId;
    private String connectedUserAlias;
    private String lastMessageContent;
    private Instant lastMessageTimestamp;
    private int unreadMessageCount;
}
