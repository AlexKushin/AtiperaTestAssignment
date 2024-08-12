package com.atipera.okushyn.testassignment.exceptions;

public class ExceededRateLimitException extends RuntimeException {

    public ExceededRateLimitException(String message) {
        super(message);
    }
}