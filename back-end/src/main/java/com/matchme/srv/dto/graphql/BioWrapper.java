package com.matchme.srv.dto.graphql;

import com.matchme.srv.model.user.User;
import com.matchme.srv.model.user.profile.Hobby;
import com.matchme.srv.model.user.profile.UserGenderEnum;
import java.util.List;

public class BioWrapper {
  private final User user;

  public BioWrapper(User user) {
    this.user = user;
  }

  public User getUser() {
    return user;
  }

  public UserGenderEnum getGender() {
    return user.getProfile() != null && user.getProfile().getAttributes() != null
        ? user.getProfile().getAttributes().getGender()
        : null;
  }

  public String getBirthdate() {
    return user.getProfile() != null && user.getProfile().getAttributes() != null
        ? user.getProfile().getAttributes().getBirthdate().toString()
        : null;
  }

  public List<Double> getLocation() {
    return user.getProfile() != null && user.getProfile().getAttributes() != null
        ? user.getProfile().getAttributes().getLocation()
        : null;
  }

  public List<Hobby> getHobbies() {
    return user.getProfile() != null && user.getProfile().getHobbies() != null
        ? user.getProfile().getHobbies().stream().toList()
        : List.of();
  }
}
