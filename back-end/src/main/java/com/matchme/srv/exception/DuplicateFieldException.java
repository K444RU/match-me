package com.matchme.srv.exception;

public class DuplicateFieldException extends RuntimeException {
    public DuplicateFieldException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
