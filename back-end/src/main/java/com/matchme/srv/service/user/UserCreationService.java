package com.matchme.srv.service.user;

import com.matchme.srv.dto.request.SignupRequestDTO;
import com.matchme.srv.dto.request.UserParametersRequestDTO;
import com.matchme.srv.model.user.User;
import com.matchme.srv.model.user.activity.ActivityLog;

public interface UserCreationService {
  ActivityLog createUser(SignupRequestDTO signUpRequest);

  void verifyAccount(Long userId, int verificationCode);

  ActivityLog setUserParameters(Long userId, UserParametersRequestDTO parameters);

  void assignDefaultRole(User user);

  void removeUserByEmail(String email);
}
