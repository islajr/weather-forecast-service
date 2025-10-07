package org.project.weatherforecastservice.dto.response;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record IndividualWeatherForecast(
        LocalDate date,
        double temperature,
        int humidity,
        double windSpeed,
        String conditions,
        String description
) {
}
