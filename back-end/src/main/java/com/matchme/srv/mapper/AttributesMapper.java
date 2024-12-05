package com.matchme.srv.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

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
  @Mapping(source = "gender_self", target = "gender")
  @Mapping(source = "birthDate", target = "birthDate")
  @Mapping(target = "location", expression = "java(List.of(parameters.longitude(), parameters.latitude()))")
  UserAttributes toEntity(UserParametersRequestDTO parameters);
}
