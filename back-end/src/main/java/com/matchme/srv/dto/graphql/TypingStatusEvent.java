package com.matchme.srv.dto.graphql;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TypingStatusEvent {
  @NotNull private Long connectionId;
  @NotNull private Long senderId;
  @NotNull private Boolean isTyping;
}
