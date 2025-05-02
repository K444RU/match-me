package com.matchme.srv.dto.graphql;

import com.matchme.srv.dto.response.ChatMessageResponseDTO;
import com.matchme.srv.dto.response.MessageEventDTO;
import com.matchme.srv.model.message.MessageEventTypeEnum;

public class ChatMessageWrapper {
  private final ChatMessageResponseDTO message;

  public ChatMessageWrapper(ChatMessageResponseDTO message) {
    if (message == null) {
      throw new IllegalArgumentException(
          "ChatMessageResponseDTO cannot be null for ChatMessageWrapper");
    }
    this.message = message;
  }

  public String getMessageId() {
    return String.valueOf(message.getMessageId());
  }

  public String getConnectionId() {
    return String.valueOf(message.getConnectionId());
  }

  public String getSenderId() {
    return String.valueOf(message.getSenderId());
  }

  public String getSenderAlias() {
    return message.getSenderAlias();
  }

  public String getContent() {
    return message.getContent();
  }

  public String getCreatedAt() {
    return message.getCreatedAt().toString();
  }

  public MessageEventWrapper getEvent() {
    return new MessageEventWrapper(message.getEvent());
  }

  public static class MessageEventWrapper {
    private final MessageEventDTO event;

    public MessageEventWrapper(MessageEventDTO event) {
      this.event = event;
    }

    public MessageEventTypeEnum getType() {
      return event.getType();
    }

    public String getTimestamp() {
      return event.getTimestamp() != null ? event.getTimestamp().toString() : null;
    }
  }
}
