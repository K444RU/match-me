package com.matchme.srv.mapper;

import org.springframework.stereotype.Component;

import com.matchme.srv.dto.response.SettingsResponseDTO;
import com.matchme.srv.model.user.User;

@Component
public class SettingsMapper {
  
  public SettingsResponseDTO toDTO(User user) {
    SettingsResponseDTO dto = new SettingsResponseDTO();
    dto.setEmail(user.getEmail());
    dto.setNumber(user.getNumber());
    dto.setPassword(user.getUserAuth().getPassword());
    return dto;
  }

  public User toEntity(SettingsResponseDTO dto, User user) {

    return user;
  }

}
