package org.project.weatherforecastservice.entity;

import java.time.LocalDateTime;

public abstract class Weather {

    private double temperature;
    private double humidity;
    private double windSpeed;
    private double windDirection;
    private double pressure;
    private double weatherMain;
    private String weatherDescription;
    private LocalDateTime timestamp;
    private LocalDateTime sunrise;
    private LocalDateTime sunset;
}
