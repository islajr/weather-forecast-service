package org.project.weatherforecastservice.dto.response;

import lombok.Builder;
import org.project.weatherforecastservice.entity.City;
import org.project.weatherforecastservice.entity.Forecast;

import java.util.List;

@Builder
public record WeatherForecastResponse(
        String city,
        String country,
        List<IndividualWeatherForecast> forecasts
) {
    public static WeatherForecastResponse generateRawResponse(List<Forecast> forecasts, City city) {
        return WeatherForecastResponse.builder()
                .city(city.getName())
                .country(city.getCountry())
                .forecasts(forecasts.stream().map(
                        forecast -> IndividualWeatherForecast.builder()
                                    .date(forecast.getForecastDate())
                                    .temperature(forecast.getTemperature())
                                    .humidity(forecast.getHumidity())
                                    .windSpeed(forecast.getWindSpeed())
                                    .conditions(forecast.getWeatherCondition())
                                    .description(forecast.getWeatherDescription())
                                .build()
                            ).toList()
                )
                .build();
    }
}
