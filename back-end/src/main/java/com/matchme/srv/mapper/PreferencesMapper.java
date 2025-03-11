package com.matchme.srv.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.matchme.srv.dto.request.UserParametersRequestDTO;
import com.matchme.srv.dto.request.settings.PreferencesSettingsRequestDTO;
import com.matchme.srv.model.user.profile.user_preferences.UserPreferences;

@Mapper(componentModel = "spring", imports = {
        java.util.List.class
})
public interface PreferencesMapper {

    // initial user setup
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userProfile", ignore = true)
    @Mapping(target = "preferenceChangeLog", ignore = true)
    @Mapping(target = "gender", ignore = true)
    @Mapping(source = "age_min", target = "ageMin")
    @Mapping(source = "age_max", target = "ageMax")
    @Mapping(source = "distance", target = "distance")
    @Mapping(source = "probability_tolerance", target = "probabilityTolerance")
    UserPreferences toEntity(@MappingTarget UserPreferences entity, UserParametersRequestDTO parameters);

    // settings updates
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userProfile", ignore = true)
    @Mapping(target = "preferenceChangeLog", ignore = true)
    @Mapping(target = "gender", ignore = true)
    @Mapping(source = "age_min", target = "ageMin")
    @Mapping(source = "age_max", target = "ageMax")
    @Mapping(source = "distance", target = "distance")
    @Mapping(source = "probability_tolerance", target = "probabilityTolerance")
    UserPreferences toEntity(@MappingTarget UserPreferences entity, PreferencesSettingsRequestDTO parameters);
}
