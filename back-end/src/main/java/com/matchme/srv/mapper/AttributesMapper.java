package com.matchme.srv.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.matchme.srv.dto.request.UserParametersRequestDTO;
import com.matchme.srv.dto.request.settings.AttributesSettingsRequestDTO;
import com.matchme.srv.model.user.profile.user_attributes.UserAttributes;

@Mapper(componentModel = "spring", imports = {
        java.util.List.class
})
public interface AttributesMapper {

    // initial user setup
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userProfile", ignore = true)
    @Mapping(target = "attributeChangeLog", ignore = true)
    @Mapping(source = "gender_self", target = "gender")
    @Mapping(source = "birth_date", target = "birthdate")
    @Mapping(target = "location", ignore = true)
    UserAttributes toEntity(@MappingTarget UserAttributes entity, UserParametersRequestDTO parameters);

    // settings updates
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userProfile", ignore = true)
    @Mapping(target = "attributeChangeLog", ignore = true)
    @Mapping(source = "gender_self", target = "gender")
    @Mapping(source = "birth_date", target = "birthdate")
    @Mapping(target = "location", ignore = true)
    UserAttributes toEntity(@MappingTarget UserAttributes entity, AttributesSettingsRequestDTO parameters);
}
