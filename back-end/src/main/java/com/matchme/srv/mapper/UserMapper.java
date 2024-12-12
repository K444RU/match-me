package com.matchme.srv.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.matchme.srv.dto.response.UserResponseDTO;
import com.matchme.srv.model.user.User;


@Mapper(componentModel = "spring")
public interface UserMapper {

  @Mapping(source = "email", target = "email")
  @Mapping(source = "number", target = "number")
  UserResponseDTO toUserParametersDTO(User user);

  //mapping from specific DTO to Specific entity
}
