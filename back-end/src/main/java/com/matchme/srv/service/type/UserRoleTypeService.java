package com.matchme.srv.service.type;

import org.springframework.stereotype.Service;

import com.matchme.srv.exception.ResourceNotFoundException;
import com.matchme.srv.model.user.UserRoleType;
import com.matchme.srv.repository.UserRoleTypeRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserRoleTypeService {

    private final UserRoleTypeRepository userRoleTypeRepository;

    public UserRoleType getByName(String name) {
        return userRoleTypeRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Role"));
    }
}
