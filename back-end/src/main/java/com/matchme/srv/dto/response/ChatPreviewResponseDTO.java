package com.matchme.srv.dto.response;

import lombok.*;

import java.time.Instant;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChatPreviewResponseDTO {
    private Long connectionId;
    private Long connectedUserId;
    private String connectedUserAlias;
    private String connectedUserFirstName;
    private String connectedUserLastName;
    private String connectedUserProfilePicture;
    private String lastMessageContent;
    private Instant lastMessageTimestamp;
    private int unreadMessageCount;
}

