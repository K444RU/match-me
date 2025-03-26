package com.matchme.srv.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/*
Online Status
For clients to send a WebSocket message when they come online/offline.
The server broadcasts it to the other participant.
The receiving client updates the UI to show “User is online/offline”.
*/
@Data
public class OnlineStatusResponseDTO {
    @NotNull
    private Long connectionId;
    @NotNull
    private Long userId;
    @NotNull
    private Boolean isOnline;
}
