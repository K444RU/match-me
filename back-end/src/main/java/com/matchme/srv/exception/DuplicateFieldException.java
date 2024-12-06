package com.matchme.srv.exception;

public class DuplicateFieldException extends RuntimeException {
    private final String fieldName;

    public DuplicateFieldException(String fieldName, String errorMessage, Throwable err) {
        super(errorMessage, err);
        this.fieldName = fieldName;
    }

    public DuplicateFieldException(String fieldName, String errorMessage) {
        super(errorMessage);
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
