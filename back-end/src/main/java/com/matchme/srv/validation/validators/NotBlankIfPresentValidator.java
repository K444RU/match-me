package com.matchme.srv.validation.validators;

import com.matchme.srv.validation.annotations.NotBlankIfPresent;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NotBlankIfPresentValidator implements ConstraintValidator<NotBlankIfPresent, String> {
  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    return value == null || !value.trim().isEmpty(); 
  }
  
}
