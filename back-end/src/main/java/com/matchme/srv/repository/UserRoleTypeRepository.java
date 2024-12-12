package com.matchme.srv.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.matchme.srv.model.user.*;

public interface UserRoleTypeRepository extends JpaRepository<UserRoleType, Long> {
    
    Optional<UserRoleType> findByName(String name);

}
