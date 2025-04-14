package com.matchme.srv.model.enums;

/**
 * Represents the possible states of a user account.
 */
public enum UserState {
    /** Initial state after registration, before email verification or profile completion. */
    NEW,
    /** User has registered but not yet verified their email (if verification is enabled). */
    UNVERIFIED,
    /** User has verified their email but not completed their profile. */
    PROFILE_INCOMPLETE,
    /** User has completed registration, verification (if any), and profile setup. Fully active. */
    ACTIVE,
    /** Account is temporarily suspended by an admin. */
    SUSPENDED,
    /** Account is inactive due to prolonged lack of activity. */
    DORMANT,
    /** Account has been permanently disabled by the user or an admin. */
    DISABLED,
    /** Account is pending some action (e.g., admin approval). */
    PENDING,
    /** User has completed email verification (used if profile completion is a separate step). */
    VERIFIED
}