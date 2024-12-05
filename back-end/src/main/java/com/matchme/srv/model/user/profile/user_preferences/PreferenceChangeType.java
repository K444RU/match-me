package com.matchme.srv.model.user.profile.user_preferences;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "preference_change_types")
public record PreferenceChangeType(

        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id,

        @Column(length = 50, unique = true) String name

) {
}