package com.matchme.srv.validation.validators;

import java.util.Set;

import com.matchme.srv.validation.annotations.ValidGender;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidGenderValidator implements ConstraintValidator<ValidGender, String> {

  private static final Set<String> VALID_GENDERS = Set.of("male", "female", "other", "everybody");

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    return value == null || VALID_GENDERS.contains(value);
  }
  
}
