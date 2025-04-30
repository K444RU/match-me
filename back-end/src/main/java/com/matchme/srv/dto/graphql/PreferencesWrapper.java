package com.matchme.srv.dto.graphql;

import com.matchme.srv.model.user.profile.UserGenderEnum;
import com.matchme.srv.model.user.profile.user_preferences.UserPreferences;

public class PreferencesWrapper {
  private final UserPreferences preferences;

  public PreferencesWrapper(UserPreferences preferences) {
    this.preferences = preferences;
  }

  public UserGenderEnum getGender() {
    return preferences.getGender();
  }

  public Integer getAgeMin() {
    return preferences.getAgeMin();
  }

  public Integer getAgeMax() {
    return preferences.getAgeMax();
  }

  public Integer getDistance() {
    return preferences.getDistance();
  }
}
