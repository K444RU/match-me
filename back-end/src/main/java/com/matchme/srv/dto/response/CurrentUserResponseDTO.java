package com.matchme.srv.dto.response;

import java.util.Set;

import com.matchme.srv.model.user.UserRoleType;

import lombok.Builder;

@Builder
public record CurrentUserResponseDTO(
        Long id,
        String firstName,
        String lastName,
        String alias,
        Set<UserRoleType> role) {
}
