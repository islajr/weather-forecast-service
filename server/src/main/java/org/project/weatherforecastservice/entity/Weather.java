package org.project.weatherforecastservice.entity;

import jakarta.persistence.Column;

import java.time.LocalDateTime;

public abstract class Weather {

    @Column(nullable = false, name = "temperature")
    private double temperature;

    @Column(name = "humidity", nullable = false)
    private int humidity;

    @Column(nullable = false, name = "wind_speed")
    private double windSpeed;

    @Column(nullable = false, name = "wind_direction")
    private int windDirection;

    @Column(nullable = false, name = "pressure")
    private int pressure;

    @Column(nullable = false, name = "conditions")
    private String weatherCondition;

    @Column(nullable = false, name = "description")
    private String weatherDescription;

    @Column(nullable = false, name = "sunrise")
    private LocalDateTime sunrise;

    @Column(nullable = false, name = "sunset")
    private LocalDateTime sunset;
}
