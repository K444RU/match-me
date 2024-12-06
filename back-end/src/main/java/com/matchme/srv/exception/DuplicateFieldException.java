package com.matchme.srv.exception;

// if (userRepository.existsByEmail(signUpRequest.getEmail())) {
//     throw new DuplicateFieldException(
//         "email",
//         "Email already exists"
//     );
// }

// "error": "Email already exists"

public class DuplicateFieldException extends RuntimeException {
    private final String fieldName;

    public DuplicateFieldException(String fieldName, String errorMessage) {
        super(errorMessage);
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
