package com.matchme.srv.validation.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.matchme.srv.validation.validators.ValidGenderValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidGenderValidator.class)
public @interface ValidGender {
  String message() default "Invalid gender value. Must be 'male', 'female' or 'other'";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
