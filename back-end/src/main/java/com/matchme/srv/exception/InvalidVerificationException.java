package com.matchme.srv.exception;

// Backlog exception for when we finally implement email verification

// if (!user.getVerificationCode().equals(verificationCode)) {
//     throw new InvalidVerificationException(
//         "Invalid verification code", 
//         verificationCode
//     );
// }

// "error": "Invalid Verification Code 123"

public class InvalidVerificationException extends RuntimeException {
    public InvalidVerificationException(String code) {
        super("Invalid Verification Code " + code);
    }
}