package com.matchme.srv.dto.response;

import com.matchme.srv.model.message.MessageEventTypeEnum;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageStatusUpdateDTO {
  private Long messageId;
  private Long connectionId;
  private MessageEventTypeEnum status;
  private Instant timestamp;
}
