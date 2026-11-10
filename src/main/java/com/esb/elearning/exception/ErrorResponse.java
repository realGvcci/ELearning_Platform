package com.esb.elearning.exception;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

/** Uniform JSON body returned for every error. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> validationErrors) {
}
