package org.project.weatherforecastservice.dto.response;

import lombok.Builder;

import java.util.Map;

@Builder
public record OpenWeatherForecastCity(
        Long id,
        String name,
        Map<String, Double> coord,
        String country,
        long population,
        int timeZone,
        long sunrise,
        long sunset
) {
}
