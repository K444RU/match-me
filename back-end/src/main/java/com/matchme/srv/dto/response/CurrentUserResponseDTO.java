package com.matchme.srv.dto.response;

import com.matchme.srv.model.enums.UserState;
import com.matchme.srv.model.user.UserRoleType;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.Set;

@Builder
public record CurrentUserResponseDTO(
        @NotNull Long id,
        @NotNull String email,
        @NotNull String firstName,
        @NotNull String lastName,
        String alias,
        String aboutMe,
        Set<HobbyResponseDTO> hobbies,
        String profilePicture,
        Set<UserRoleType> role,
        String profileLink,
        UserState state
) {}
