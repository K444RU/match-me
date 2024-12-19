package com.matchme.srv.dto.response;

import com.matchme.srv.model.user.UserRoleType;
import com.matchme.srv.model.user.UserStateTypes;
import com.matchme.srv.model.user.activity.ActivityLog;
import com.matchme.srv.model.user.profile.UserProfile;
import com.matchme.srv.model.user.profile.user_score.UserScore;

/**
* Custom DTO so we don't return UserAuth
*
 * @param id
 * @param email
 * @param number
 * @param state - current state of the user account
 * @param roles - set of roles assigned to the user
 * @param activity - set of user activity logs
 * @param profile
 * @param score
* @return UserResponseDTO
* @see  UserStateTypes
* @see  UserRoleType
* @see  ActivityLog
* @see  UserProfile
* @see  UserScore
*/
public record UserResponseDTO(
  Long id,
  String email,
  String number
  // UserStateTypes state,
  // Set<UserRoleType> roles,
  // Set<ActivityLog> activity,
  // UserProfile profile,
  // UserScore score
) {}
