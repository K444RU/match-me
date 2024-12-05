package com.matchme.srv.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.matchme.srv.model.user.profile.user_attributes.AttributeChangeType;

public interface AttributeChangeTypeRepository extends JpaRepository<AttributeChangeType, Long> {
    Optional<AttributeChangeType> findByName(String name);
}
