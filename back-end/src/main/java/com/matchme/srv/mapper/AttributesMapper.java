package com.matchme.srv.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


import com.matchme.srv.dto.request.UserParametersRequestDTO;
import com.matchme.srv.model.user.profile.user_attributes.UserAttributes;

@Mapper(
    componentModel = "spring",
    imports = {
        java.util.List.class
    }
)
public interface AttributesMapper {
  
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "userProfile", ignore = true)
  @Mapping(target = "attributeChangeLog", ignore = true)
  @Mapping(target = "gender", ignore = true)
  @Mapping(source = "birth_date", target = "birth_date")
  @Mapping(target = "location", ignore = true)
  UserAttributes toEntity(@MappingTarget UserAttributes entity, UserParametersRequestDTO parameters);
}
