package com.matchme.srv.dto.graphql;

import com.matchme.srv.model.user.User;

public class UserGraphqlDTO {

    private final User user;

    public UserGraphqlDTO(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null for UserGraphqlDTO");
        }
        this.user = user;
    }

    public String getId() {
        return String.valueOf(this.user.getId());
    }

    public String getAlias() {
        return this.user.getProfile() != null ? this.user.getProfile().getAlias() : null;
    }

    public ProfileWrapper getProfile() {
        return new ProfileWrapper(this.user);
    }

    public BioWrapper getBio() {
        return new BioWrapper(this.user);
    }

    // --- Sensitive Fields (NOT Exposed) ---
    // No getEmail()
    // No getNumber()
    // No getUserAuth()
    // No getRoles()
    // No getActivity()
    // No getState()
    // No getScore()

}