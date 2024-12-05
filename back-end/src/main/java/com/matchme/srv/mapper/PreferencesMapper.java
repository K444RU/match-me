package com.matchme.srv.mapper;

import org.mapstruct.Mapper;

import com.matchme.srv.dto.request.UserParametersRequestDTO;
import com.matchme.srv.model.user.profile.user_preferences.UserPreferences;

@Mapper(componentModel = "spring")
public interface PreferencesMapper {
  
  UserPreferences toEntity(UserParametersRequestDTO parameters);
}
