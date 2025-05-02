package com.matchme.srv.dto.graphql;

import com.matchme.srv.dto.response.SettingsResponseDTO;
import com.matchme.srv.model.user.profile.UserGenderEnum;
import java.util.Set;

public class SettingsWrapper {
  private final SettingsResponseDTO settings;

  public SettingsWrapper(SettingsResponseDTO settings) {
    if (settings == null) {
      throw new IllegalArgumentException("SettingsResponseDTO cannot be null for SettingsWrapper");
    }
    this.settings = settings;
  }

  public String getEmail() {
    return settings.email();
  }

  public String getNumber() {
    return settings.number();
  }

  public String getFirstName() {
    return settings.firstName();
  }

  public String getLastName() {
    return settings.lastName();
  }

  public String getAlias() {
    return settings.alias();
  }

  public String getAboutMe() {
    return settings.aboutMe();
  }

  public Set<Long> getHobbies() {
    return settings.hobbies();
  }

  public UserGenderEnum getGenderSelf() {
    return settings.genderSelf();
  }

  public String getBirthDate() {
    return settings.birthDate();
  }

  public String getCity() {
    return settings.city();
  }

  public Double getLongitude() {
    return settings.longitude();
  }

  public Double getLatitude() {
    return settings.latitude();
  }

  public UserGenderEnum getGenderOther() {
    return settings.genderOther();
  }

  public Integer getAgeMin() {
    return settings.ageMin();
  }

  public Integer getAgeMax() {
    return settings.ageMax();
  }

  public Integer getDistance() {
    return settings.distance();
  }

  public Double getProbabilityTolerance() {
    return settings.probabilityTolerance();
  }

  public String getProfilePicture() {
    return settings.profilePicture();
  }
}
