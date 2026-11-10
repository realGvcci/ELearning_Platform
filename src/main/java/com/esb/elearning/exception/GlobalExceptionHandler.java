package com.esb.elearning.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

/** Converts every exception into a clean JSON error with the right HTTP status. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ ResourceNotFoundException.class, EntityNotFoundException.class })
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req, null);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleUnknownUrl(NoResourceFoundException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, "No endpoint found for " + req.getRequestURI(), req, null);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateResourceException ex, HttpServletRequest req) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), req, null);
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRule(BusinessRuleException ex, HttpServletRequest req) {
        return build(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), req, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(fe -> errors.putIfAbsent(fe.getField(), fe.getDefaultMessage()));
        return build(HttpStatus.BAD_REQUEST, "Validation failed", req, errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraint(ConstraintViolationException ex, HttpServletRequest req) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getConstraintViolations()
                .forEach(v -> errors.putIfAbsent(v.getPropertyPath().toString(), v.getMessage()));
        return build(HttpStatus.BAD_REQUEST, "Validation failed", req, errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadable(HttpMessageNotReadableException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, "Malformed JSON or invalid value (check enum values and date/number formats)", req, null);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, "Invalid value for parameter '" + ex.getName() + "'", req, null);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParam(MissingServletRequestParameterException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, "Missing required parameter '" + ex.getParameterName() + "'", req, null);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex, HttpServletRequest req) {
        return build(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage(), req, null);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMediaType(HttpMediaTypeNotSupportedException ex, HttpServletRequest req) {
        return build(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Content type not supported, use application/json", req, null);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleIntegrity(DataIntegrityViolationException ex, HttpServletRequest req) {
        return build(HttpStatus.CONFLICT, "Operation violates a database constraint (duplicate value or linked data)", req, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleOther(Exception ex, HttpServletRequest req) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error", req, null);
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message,
            HttpServletRequest req, Map<String, String> validationErrors) {
        ErrorResponse body = new ErrorResponse(LocalDateTime.now(), status.value(),
                status.getReasonPhrase(), message, req.getRequestURI(), validationErrors);
        return ResponseEntity.status(status).body(body);
    }
}
