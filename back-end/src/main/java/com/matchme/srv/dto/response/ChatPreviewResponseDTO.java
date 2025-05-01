package com.matchme.srv.dto.response;

import lombok.*;

import java.time.Instant;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChatPreviewResponseDTO {
    @NotNull private Long connectionId;
    @NotNull private Long connectedUserId;
    @NotNull private String connectedUserAlias;
    @NotNull private String connectedUserFirstName;
    @NotNull private String connectedUserLastName;
    @NotNull private String connectedUserProfilePicture;
    private String lastMessageContent;
    private Instant lastMessageTimestamp;
    @NotNull private int unreadMessageCount;
}

