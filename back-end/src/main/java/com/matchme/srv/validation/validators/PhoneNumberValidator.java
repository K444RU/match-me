package com.matchme.srv.validation.validators;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.matchme.srv.validation.annotations.ValidPhoneNumber;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, String> {
    private final PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

    @Override
    public boolean isValid(String phoneField, ConstraintValidatorContext context) {
        // If the value is null or blank, consider it valid here,
        // because @NotBlank will handle it.
        if (phoneField == null || phoneField.isBlank()) {
            return true;
        }

        try {
            var phoneNumber = phoneUtil.parse(phoneField, null);
            return phoneUtil.isValidNumber(phoneNumber);
        } catch (NumberParseException e) {
            return false;
        }
    }
}
