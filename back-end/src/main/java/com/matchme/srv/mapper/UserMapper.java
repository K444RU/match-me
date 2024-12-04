package com.matchme.srv.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.matchme.srv.dto.request.LoginRequestDTO;
import com.matchme.srv.model.user.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

  @Mapping(source = "email", target = "email") // source = field from DTO, target 
  User toEntity(LoginRequestDTO loginRequest);

  //mapping from specific DTO to Specific entity
}
