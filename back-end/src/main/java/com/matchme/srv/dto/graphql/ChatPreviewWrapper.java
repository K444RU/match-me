package com.matchme.srv.dto.graphql;

import com.matchme.srv.dto.response.ChatPreviewResponseDTO;

public class ChatPreviewWrapper {
  private final ChatPreviewResponseDTO preview;

  public ChatPreviewWrapper(ChatPreviewResponseDTO preview) {
    if (preview == null) {
      throw new IllegalArgumentException(
          "ChatPreviewResponseDTO cannot be null for ChatPreviewWrapper");
    }
    this.preview = preview;
  }

  public String getConnectionId() {
    return String.valueOf(preview.getConnectionId());
  }

  public String getConnectedUserId() {
    return String.valueOf(preview.getConnectedUserId());
  }

  public String getConnectedUserAlias() {
    return preview.getConnectedUserAlias();
  }

  public String getConnectedUserFirstName() {
    return preview.getConnectedUserFirstName();
  }

  public String getConnectedUserLastName() {
    return preview.getConnectedUserLastName();
  }

  public String getConnectedUserProfilePicture() {
    return preview.getConnectedUserProfilePicture();
  }

  public String getLastMessageContent() {
    return preview.getLastMessageContent();
  }

  public String getLastMessageTimestamp() {
    return preview.getLastMessageTimestamp() != null
        ? preview.getLastMessageTimestamp().toString()
        : null;
  }

  public int getUnreadMessageCount() {
    return preview.getUnreadMessageCount();
  }
}
