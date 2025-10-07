package org.project.weatherforecastservice.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record WeatherForecastResponse(
        String city,
        String country,
        List<IndividualWeatherForecast> forecasts
) {
}
