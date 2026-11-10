package com.esb.elearning.exception;

/** Thrown when a business rule is violated (course not published, ...) -> HTTP 422. */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
