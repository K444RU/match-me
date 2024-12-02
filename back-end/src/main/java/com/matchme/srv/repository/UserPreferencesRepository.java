package com.matchme.srv.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.matchme.srv.model.user.profile.user_preferences.UserPreferences;

public interface UserPreferencesRepository extends JpaRepository<UserPreferences, Long> {

}