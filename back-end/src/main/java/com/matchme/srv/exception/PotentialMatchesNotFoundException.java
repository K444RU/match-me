package com.matchme.srv.exception;

public class PotentialMatchesNotFoundException extends RuntimeException {
    public PotentialMatchesNotFoundException(String userId) {
        super(userId + " not found");
    }
}
