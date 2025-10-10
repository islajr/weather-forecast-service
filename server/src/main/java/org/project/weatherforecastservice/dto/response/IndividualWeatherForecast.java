package org.project.weatherforecastservice.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record IndividualWeatherForecast(
        LocalDateTime date,
        double temperature,
        int humidity,
        double windSpeed,
        String conditions,
        String description
) {
}
