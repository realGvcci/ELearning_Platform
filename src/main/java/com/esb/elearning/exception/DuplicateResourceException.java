package com.esb.elearning.exception;

/** Thrown when a unique value already exists (email, name...) -> HTTP 409. */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
