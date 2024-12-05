package com.matchme.srv.model.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_state_types")
public record UserStateTypes(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id,

    @Column(length = 50, unique = true) String name

) {
}