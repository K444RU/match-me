package com.matchme.srv.security.jwt;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import com.matchme.srv.security.services.UserDetailsImpl;

@Component
public class SecurityUtils {
    public Long getCurrentUserId(Authentication authentication) {
        return ((UserDetailsImpl) authentication.getPrincipal()).getId();
    }
}