package com.matchme.srv.dto.response;

import java.time.Instant;

import com.matchme.srv.model.message.MessageEventTypeEnum;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MessageEventDTO {
    @NotNull MessageEventTypeEnum type;
    @NotNull Instant timestamp;
}
