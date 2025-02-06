package com.matchme.srv.service.user.validation;

import com.matchme.srv.exception.DuplicateFieldException;
import com.matchme.srv.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserValidationService {
  private final UserRepository userRepository;

  /**
   * Checks if the given email or phone number is used by a *different* user. If so, throws a
   * DuplicateFieldException.
   *
   * @param email - new email to be used
   * @param number - new phone number to be used
   * @param userId - the user doing the update (can be null for new users)
   */
  public void validateUniqueEmailAndNumber(String email, String number, Long userId) {

    userRepository
        .findByEmailIgnoreCase(email)
        .filter(existingUser -> !existingUser.getId().equals(userId))
        .ifPresent(
            u -> {
              throw new DuplicateFieldException("email", "Email already exists");
            });

    userRepository
        .findByNumber(number)
        .filter(existingUser -> !existingUser.getId().equals(userId))
        .ifPresent(
            u -> {
              throw new DuplicateFieldException("number", "Phone number already exists");
            });
  }
}
