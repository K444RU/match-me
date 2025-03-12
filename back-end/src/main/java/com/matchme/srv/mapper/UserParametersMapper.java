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
  @Mapping(source = "user.profile.first_name", target = "first_name")
  @Mapping(source = "user.profile.last_name", target = "last_name")
  @Mapping(source = "user.profile.alias", target = "alias")
  @Mapping(target = "hobbies", expression = "java(user.getProfile().getHobbies().stream().map(hobby -> hobby.getId()).collect(java.util.stream.Collectors.toSet()))")
  @Mapping(source = "user.profile.city", target = "city")
  @Mapping(target = "gender_self", expression = "java(userAttributes.getGender().getId())")
  @Mapping(source = "userAttributes.birthdate", target = "birth_date")
  @Mapping(target = "longitude", expression = "java(userAttributes.getLocation() != null ? userAttributes.getLocation().get(0) : null)")
  @Mapping(target = "latitude", expression = "java(userAttributes.getLocation() != null ? userAttributes.getLocation().get(1) : null)")
  @Mapping(target = "gender_other", expression = "java(userPreferences.getGender().getId())")
  @Mapping(source = "userPreferences.ageMin", target = "age_min")
  @Mapping(source = "userPreferences.ageMax", target = "age_max")
  @Mapping(source = "userPreferences.distance", target = "distance")
  @Mapping(source = "userPreferences.probabilityTolerance", target = "probability_tolerance")
  @Mapping(source = "userAuth.password", target = "password")
  @Mapping(source = "user.profile.profilePicture", target = "profilePicture")
  UserParametersResponseDTO toUserParametersDTO(User user, UserAttributes userAttributes,
      UserPreferences userPreferences, UserAuth userAuth);

}
