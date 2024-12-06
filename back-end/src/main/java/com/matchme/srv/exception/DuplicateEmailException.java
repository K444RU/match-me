package com.matchme.srv.exception;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
