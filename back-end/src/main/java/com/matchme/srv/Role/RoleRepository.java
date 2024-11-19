package com.matchme.srv.Role;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.matchme.srv.ERole.ERole;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
