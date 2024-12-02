package com.matchme.srv.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.matchme.srv.model.user.*;
import com.matchme.srv.model.user.Role.UserRole;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(UserRole name);
}
