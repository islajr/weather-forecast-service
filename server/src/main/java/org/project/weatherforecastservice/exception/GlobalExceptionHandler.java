package org.project.weatherforecastservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.weatherforecastservice.exception.exceptions.CityNotFoundException;
import org.project.weatherforecastservice.exception.exceptions.FailedToGetWeatherException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    // 502 - Failed to Get Weather
    @ExceptionHandler(value = FailedToGetWeatherException.class)
    public ResponseEntity<ErrorResponse> handleFailedToGetWeatherException(FailedToGetWeatherException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(new ErrorResponse(HttpStatus.BAD_GATEWAY, ex.getMessage(), request, LocalDateTime.now()));
    }

    // 405 -- Method Not Allowed
    @ExceptionHandler(value = MethodNotAllowedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotAllowedException(MethodNotAllowedException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(new ErrorResponse(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage(), request, LocalDateTime.now()));
    }

    // 404 -- City Not Found
    @ExceptionHandler(value = CityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCityNotFoundException(CityNotFoundException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request, LocalDateTime.now()));
    }

    // 400 -- No Resource Found
    @ExceptionHandler(value = NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request, LocalDateTime.now()));
    }

    // 500 -- Fallback Exception Handler
    @ExceptionHandler(value = CityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request, LocalDateTime.now()));
    }

}
