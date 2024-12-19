package com.matchme.srv.dto.response;

import java.util.Set;

import lombok.Builder;

@Builder
public record ConnectionResponseDTO(
    Long id,
    Set<UserResponseDTO> users
//   Set<ConnectionState> connectionStates,
//   Set<ConnectionResult> connectionResults,
//   Set<UserMessage> userMessages
) {}
