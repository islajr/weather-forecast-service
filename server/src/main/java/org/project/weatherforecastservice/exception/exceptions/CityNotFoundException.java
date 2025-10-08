package org.project.weatherforecastservice.exception.exceptions;

public class CityNotFoundException extends RuntimeException {

    String message;

    public CityNotFoundException(String message) {
        this.message = message;
    }
}
