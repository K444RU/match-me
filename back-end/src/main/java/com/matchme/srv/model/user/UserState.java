package com.matchme.srv.model.user;

public enum UserState {
  UNVERIFIED, // without e-mail verification
  NEW,
  PENDING, // account is problematic, has many warnings etc. 
  SUSPENDED, // account suspended due to...
  SLEEP, // account is dormant, not using the application for a longer period 
  DISABLED, // account is disabled by the user
}
