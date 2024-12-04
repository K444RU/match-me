package com.matchme.srv.model.user;

public enum UserState {
  UNVERIFIED, // without e-mail verification
  VERIFIED,
  NEW,
  ACTIVE, // as opposed to dormant
  PENDING, // account is problematic, has many warnings etc. 
  SUSPENDED, // account suspended due to...
  DORMANT, // account is dormant/sleeping, not using the application for a longer period 
  DISABLED, // account is disabled by the user
}
