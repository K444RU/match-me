package com.matchme.srv.dto.graphql;

import com.matchme.srv.model.user.User;
import java.util.Base64;

public class ProfileWrapper {
  private final User user;

  public ProfileWrapper(User user) {
    this.user = user;
  }

  public User getUser() {
    return user;
  }

  public String getFirstName() {
    return user.getProfile() != null ? user.getProfile().getFirst_name() : null;
  }

  public String getLastName() {
    return user.getProfile() != null ? user.getProfile().getLast_name() : null;
  }

  public String getAlias() {
    return user.getProfile() != null ? user.getProfile().getAlias() : null;
  }

  public String getCity() {
    return user.getProfile() != null ? user.getProfile().getCity() : null;
  }

  public String getProfilePicture() {
    if (user.getProfile() == null || user.getProfile().getProfilePicture() == null) {
      return null;
    }
    return Base64.getEncoder().encodeToString(user.getProfile().getProfilePicture());
  }

  public String getAboutMe() {
    return user.getProfile() != null ? user.getProfile().getAboutMe() : null;
  }

  public PreferencesWrapper getPreferences() {
    return user.getProfile() != null && user.getProfile().getPreferences() != null
        ? new PreferencesWrapper(user.getProfile().getPreferences())
        : null;
  }
}
