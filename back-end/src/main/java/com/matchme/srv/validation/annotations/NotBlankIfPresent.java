package com.matchme.srv.validation.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.matchme.srv.validation.validators.NotBlankIfPresentValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;


@Target({ElementType.FIELD}) // Specifies where it's applied
@Retention(RetentionPolicy.RUNTIME) // Specifies runtime availability
@Constraint(validatedBy = NotBlankIfPresentValidator.class) // Links to validation logic
public @interface NotBlankIfPresent { // Declares a custom annotation
  String message() default "Field cannot be blank if provided"; // error message

  Class<?>[] groups() default {}; // grouping validations - not needed now/yet

  Class<? extends Payload>[] payload() default {}; // allows attaching metadata - rarely used
}
