package com.dev.explainor.genesis.config;

import com.dev.explainor.genesis.api.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleValidationError(IllegalArgumentException exception) {
        log.warn("Validation error: {}", exception.getMessage());
        ErrorResponse error = ErrorResponse.of("VALIDATION_ERROR", exception.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getAllErrors().stream()
            .findFirst()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .orElse("Validation failed");
        log.warn("Method argument validation error: {}", message);
        ErrorResponse error = ErrorResponse.of("VALIDATION_ERROR", message);
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException exception) {
        log.warn("Malformed JSON request: {}", exception.getMessage());
        ErrorResponse error = ErrorResponse.of("MALFORMED_JSON", "Request body is not valid JSON");
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException exception) {
        log.warn("Unsupported media type: {}", exception.getContentType());
        ErrorResponse error = ErrorResponse.of("UNSUPPORTED_MEDIA_TYPE", "Content-Type must be application/json");
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(error);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResponse> handleNullPointerError(NullPointerException exception) {
        log.error("Null pointer error", exception);
        ErrorResponse error = ErrorResponse.of("NULL_POINTER_ERROR", "Required value is null");
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralError(Exception exception) {
        log.error("Unexpected error occurred", exception);
        ErrorResponse error = ErrorResponse.of("INTERNAL_ERROR", "An unexpected error occurred");
        return ResponseEntity.internalServerError().body(error);
    }
}

