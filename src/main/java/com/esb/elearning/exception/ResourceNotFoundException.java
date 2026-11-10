package com.esb.elearning.exception;

/** Thrown when an entity does not exist -> HTTP 404. */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resource, Long id) {
        super(resource + " not found with id " + id);
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
