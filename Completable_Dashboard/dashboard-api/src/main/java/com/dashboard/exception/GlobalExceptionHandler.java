package com.dashboard.exception;

import com.dashboard.model.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(WidgetFetchException.class)
    public ResponseEntity<ErrorResponse> handleWidgetFetch(
            WidgetFetchException ex, HttpServletRequest request) {
        log.error("Widget fetch failed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .body(buildError(206, "Widget Fetch Failed", ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(
            IllegalArgumentException ex, HttpServletRequest request) {
        return ResponseEntity.badRequest()
                .body(buildError(400, "Bad Request", ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(
            Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception", ex);
        return ResponseEntity.internalServerError()
                .body(buildError(500, "Internal Server Error", ex.getMessage(), request.getRequestURI()));
    }

    private ErrorResponse buildError(int status, String error, String message, String path) {
        return ErrorResponse.builder()
                .status(status)
                .error(error)
                .message(message)
                .path(path)
                .timestamp(Instant.now())
                .build();
    }
}
