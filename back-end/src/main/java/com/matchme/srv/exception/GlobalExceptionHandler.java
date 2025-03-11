package com.matchme.srv.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // Handles validation errors, e.g. @NotBlank, @Size, @Email, etc.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }

    // Thrown if user has supplied an invalid password or a non-existing email on
    // login
    // "error": "Invalid credentials"
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentialsException(BadCredentialsException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Invalid credentials");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    // Thrown if user has insufficient permissions (must be authenticated)
    // "error": "Access Denied"
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<Void> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
        log.error("AuthorizationDeniedException occurred: {}", ex.getMessage()/* , ex */);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    // Thrown by spring-security
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Void> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        log.error("UsernameNotFoundException occurred: {}", ex.getMessage()/* , ex */);
        return ResponseEntity.notFound().build();
    }

    // Handles runtime exceptions
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Void> handleRuntimeException(RuntimeException ex) {
        log.error("RuntimeException occurred: {}", ex.getMessage(), ex);
        return ResponseEntity.internalServerError().build();
    }

    // SignUpRequest validation for alreadying existing emails/numbers
    // Example: if user.existsByEmail throw -> this
    // "error": "Email already exists"
    @ExceptionHandler(DuplicateFieldException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateEmailException(DuplicateFieldException ex) {
        Map<String, String> error = new HashMap<>();
        error.put(ex.getFieldName(), ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    // Repository doesn't find a value/resource/entity
    // Example: repo.findById orElseThrow -> this
    // "error": "Match not found"
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Void> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.error("ResourceNotFoundException occurred: {}", ex.getMessage(), ex);
        return ResponseEntity.notFound().build();
    }

    // Supplied verification code is invalid
    // Example: if user.getverificationcode != code throw -> this
    // "error": "Invalid Verification Code 123"
    @ExceptionHandler(InvalidVerificationException.class)
    public ResponseEntity<Map<String, String>> handleInvalidVerificationException(InvalidVerificationException ex) {
        log.error("InvalidVerificationException occurred: {}", ex.getMessage()/* , ex */);
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Please check your input fields.");
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Void> handleEntityNotFound(EntityNotFoundException ex) {
        log.error("EntityNotFoundException occurred: {}", ex.getMessage()/* , ex */);
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Void> handleIllegalArgument(IllegalArgumentException ex) {
        log.error("IllegalArgumentException occurred: {}", ex.getMessage()/* , ex */);
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Void> handleGeneral(Exception ex) {
        log.error("Exception occurred: {}", ex.getMessage()/* , ex */);
        return ResponseEntity.internalServerError().build();
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Void> handleAuthenticationException(AuthenticationException ex) {
        log.error("AuthenticationException occurred: {}", ex.getMessage()/* , ex */);
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Void> handleIllegalStateException(IllegalStateException ex) {
        log.error("IllegalStateException occurred: {}", ex.getMessage()/* , ex */);
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(ImageValidationException.class)
    public ResponseEntity<Map<String, String>> handleImageValidationException(ImageValidationException ex) {
        log.error("ImageValidationException occurred: {}", ex.getMessage());
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(PotentialMatchesNotFoundException.class)
    public ResponseEntity<Void> handlePotentialMatchesNotFoundException(PotentialMatchesNotFoundException ex) {
        log.error("PotentialMatchesNotFoundException occurred: {}", ex.getMessage(), ex);
        return ResponseEntity.notFound().build();
    }
}
