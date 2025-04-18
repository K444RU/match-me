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
        // Let @NotBlank handle empty strings
        if (phoneField == null || phoneField.isEmpty()) {
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
