package org.project.weatherforecastservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class ErrorResponse {

    private final HttpStatus httpStatus;
    private final String error;
    private final String message;
    private final String path;
    private final LocalDateTime timestamp;

    public ErrorResponse(HttpStatus status, String message, HttpServletRequest request, LocalDateTime timestamp) {
        this.httpStatus = status;
        this.error = httpStatus.getReasonPhrase();
        this.message = message;
        this.path = request.getRequestURI();
        this.timestamp = timestamp;
    }
}
