package com.matchme.srv.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/*
Typing in progress
Show a "typing" in progress indicator on the chat view 💬.
When a user starts typing a message, the recipient should see this.
It should disappear if the user stops typing for a while.

Typing Indicator
For clients to send a WebSocket message when they start/stop typing.
The server broadcasts it to the other participant.
The receiving client updates the UI to show “User is typing...” if isTyping == true.
*/
@Data
public class TypingStatusRequestDTO {
    @NotNull
    private Long connectionId;
    @NotNull
    private Long senderId;
    @NotNull
    private Boolean isTyping;
}
