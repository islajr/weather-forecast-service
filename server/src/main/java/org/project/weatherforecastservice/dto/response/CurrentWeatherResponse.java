package org.project.weatherforecastservice.dto.response;

import lombok.Builder;
import org.project.weatherforecastservice.entity.City;
import org.project.weatherforecastservice.entity.CurrentWeather;

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
    public static CurrentWeatherResponse generateRawResponse(CurrentWeather currentWeather, City city) {
        return CurrentWeatherResponse.builder()
                .city(city.getName())
                .country(city.getCountry())
                .timestamp(currentWeather.getTimestamp())
                .temperature(currentWeather.getTemperature())
                .humidity(currentWeather.getHumidity())
                .windSpeed(currentWeather.getWindSpeed())
                .windDirection(currentWeather.getWindDirection())
                .pressure(currentWeather.getPressure())
                .conditions(currentWeather.getWeatherCondition())
                .description(currentWeather.getWeatherDescription())
                .sunrise(currentWeather.getSunrise())
                .sunset(currentWeather.getSunset())
                .build();
    }
}
