package com.harshit.moviebooking.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getAllErrors()
                .forEach(error -> {

                    String fieldName = ((FieldError) error).getField();
                    String errorMessage = error.getDefaultMessage();

                    errors.put(fieldName, errorMessage);

                });

        ApiErrorResponse response = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                errors
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(MovieNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleMovieNotFoundException(
            MovieNotFoundException ex) {

        ApiErrorResponse response = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Movie Not Found",
                Map.of("message", ex.getMessage())
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException ex) {

        String detail = rootMessage(ex);
        String message = "Could not save your changes because the data is invalid.";

        if (detail != null && detail.toLowerCase().contains("numeric field overflow")) {
            message = "Could not save the rating. Average rating must be between 0 and 10.";
        }

        ApiErrorResponse response = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                Map.of("message", message)
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiErrorResponse> handleRuntimeException(RuntimeException ex) {
        String message = ex.getMessage() != null ? ex.getMessage() : "Unexpected error";
        boolean authFailure = message.toLowerCase().contains("invalid email or password")
                || message.toLowerCase().contains("account is not active")
                || message.toLowerCase().contains("please log in first");
        HttpStatus status = authFailure ? HttpStatus.UNAUTHORIZED : HttpStatus.BAD_REQUEST;

        // Avoid leaking raw SQL / JDBC details to the UI
        if (!authFailure && looksLikePersistenceError(message)) {
            message = "Could not save your changes. Please check your input and try again.";
        }

        ApiErrorResponse response = new ApiErrorResponse(
                LocalDateTime.now(),
                status.value(),
                authFailure ? "Authentication Failed" : "Bad Request",
                Map.of("message", message)
        );

        return ResponseEntity.status(status).body(response);
    }

    private static String rootMessage(Throwable ex) {
        Throwable current = ex;
        while (current.getCause() != null) {
            current = current.getCause();
        }
        return current.getMessage();
    }

    private static boolean looksLikePersistenceError(String message) {
        String lower = message.toLowerCase();
        return lower.contains("could not execute statement")
                || lower.contains("sql [")
                || lower.contains("numeric field overflow");
    }
}
