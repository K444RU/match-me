package com.matchme.srv.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.matchme.srv.dto.response.UserParametersResponseDTO;
import com.matchme.srv.model.user.User;
import com.matchme.srv.model.user.UserAuth;
import com.matchme.srv.model.user.profile.user_attributes.UserAttributes;
import com.matchme.srv.model.user.profile.user_preferences.UserPreferences;

@Mapper(componentModel = "spring")
public interface UserParametersMapper {
  
  @Mapping(source = "user.email", target = "email")
  @Mapping(source = "user.number", target = "number")
  @Mapping(target = "gender_self", expression = "java(userAttributes.getGender().getId())")
  @Mapping(source = "userAttributes.birthDate", target = "birthDate")
  @Mapping(target = "longitude", expression = "java(userAttributes.getLocation() != null ? userAttributes.getLocation().get(0) : null)")
  @Mapping(target = "latitude", expression = "java(userAttributes.getLocation() != null ? userAttributes.getLocation().get(1) : null)")
  @Mapping(target = "gender_other", expression = "java(userPreferences.getGender().getId())")
  @Mapping(source = "userPreferences.age_min", target = "age_min")
  @Mapping(source = "userPreferences.age_max", target = "age_max")
  @Mapping(source = "userPreferences.distance", target = "distance")
  @Mapping(source = "userPreferences.probabilityTolerance", target = "probabilityTolerance")
  @Mapping(source = "userAuth.password", target = "password")
  UserParametersResponseDTO toUserParametersDTO(User user, UserAttributes userAttributes, UserPreferences userPreferences, UserAuth userAuth);

}
