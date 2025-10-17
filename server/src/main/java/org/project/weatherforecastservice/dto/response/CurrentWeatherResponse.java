package org.project.weatherforecastservice.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CurrentWeatherResponse(
        String city,
        String country,
        LocalDateTime timestamp,
        double temperature,
        int humidity,
        double windSpeed,
        int windDirection,
        int pressure,
        String conditions,
        String description,
        LocalDateTime sunrise,
        LocalDateTime sunset
) {
}
