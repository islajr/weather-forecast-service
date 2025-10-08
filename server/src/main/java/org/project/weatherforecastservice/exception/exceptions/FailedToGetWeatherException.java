package org.project.weatherforecastservice.exception.exceptions;

public class FailedToGetWeatherException extends RuntimeException {

    String message;

    public FailedToGetWeatherException(String message) {
        this.message = message;
    }
}
