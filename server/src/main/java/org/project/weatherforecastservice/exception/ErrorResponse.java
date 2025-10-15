package org.project.weatherforecastservice.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record ErrorResponse(

    int httpStatus,
    String error,
    String message,
    String path,
    LocalDateTime timestamp

) {

    public ErrorResponse(int httpStatus, String error, String message, String path, LocalDateTime timestamp) {
        this.httpStatus = httpStatus;
        this.error = error;
        this.message = message;
        this.path = path;
        this.timestamp = timestamp;
    }

    public ErrorResponse(HttpStatus httpStatus, String message, String path, LocalDateTime timestamp) {
        this(httpStatus.value(), httpStatus.getReasonPhrase(), message, path, timestamp);
    }
}
