package com.matchme.srv.validation.validators;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.matchme.srv.model.user.profile.UserGenderEnum;
import com.matchme.srv.validation.annotations.ValidGender;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidGenderValidator implements ConstraintValidator<ValidGender, UserGenderEnum> {

  private static final java.util.Set<String> VALID_GENDERS = Arrays.stream(UserGenderEnum.values())
      .map(Enum::name)
      .map(String::toLowerCase)
      .collect(Collectors.toSet());

  @Override
  public boolean isValid(UserGenderEnum value, ConstraintValidatorContext context) {
    return value == null || VALID_GENDERS.contains(value.name().toLowerCase());
  }
  
}
