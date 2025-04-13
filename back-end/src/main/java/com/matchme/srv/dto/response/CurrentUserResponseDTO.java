package com.matchme.srv.dto.response;

import com.matchme.srv.model.enums.UserState;
import com.matchme.srv.model.user.UserRoleType;
import lombok.Builder;

import java.util.Set;

@Builder
public record CurrentUserResponseDTO(
        Long id,
        String email,
        String firstName,
        String lastName,
        String alias,
        String profilePicture,
        Set<UserRoleType> role,
        String profileLink,
        UserState state
) {}
