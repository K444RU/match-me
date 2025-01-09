package com.matchme.srv.dto.response;

import java.util.Set;

import com.matchme.srv.model.user.UserRoleType;

import lombok.Builder;

@Builder
public record CurrentUserResponseDTO(
        Long id,
        String email,
        String firstName,
        String lastName,
        String alias,
        String profilePicture,
        Set<UserRoleType> role) {
}
