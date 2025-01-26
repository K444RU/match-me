package com.matchme.srv.service.user.validation;

import org.springframework.stereotype.Service;
import com.matchme.srv.service.AccessValidationService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserValidationService {
    private final AccessValidationService accessValidationService;

    public void validateUserAccess(Long currentUserId, Long targetUserId) {
        accessValidationService.validateUserAccess(currentUserId, targetUserId);
    }
}
