package org.project.weatherforecastservice.dto.response;

import lombok.Builder;
import org.project.weatherforecastservice.entity.City;
import org.project.weatherforecastservice.entity.Forecast;
import org.project.weatherforecastservice.util.TimeConverter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record OpenWeatherForecastResponse(
    String cod,
    int message,
    int cnt,
    List<OpenWeatherForecastWeather> list,
    OpenWeatherForecastCity city

) {

    public City extractCity(OpenWeatherForecastResponse response) {
        return City.builder()
                .name(response.city.name())
                .longitude(response.city.coord().get("lon"))
                .latitude(response.city.coord().get("lat"))
                .country(response.city.country())
                .build();
    }

    public Forecast extractForecast(OpenWeatherForecastWeather forecast, OpenWeatherForecastCity city) {
        return Forecast.builder()
                .pressure(Float.floatToIntBits(forecast.main().get("pressure")))
                .forecastDate(time(forecast.dt(), city.timeZone()))
                .sunrise(time(city.sunrise(), city.timeZone()))
                .sunset(time(city.sunset(), city.timeZone()))
                .weatherCondition(forecast.weather().get(0).get("main"))
                .weatherDescription(forecast.weather().get(0).get("description"))
                .humidity(Float.floatToIntBits(forecast.main().get("humidity")))
                .temperature(forecast.main().get("temp"))
                .windDirection(Float.floatToIntBits(forecast.wind().get("speed")))
                .windSpeed(Float.floatToIntBits(forecast.wind().get("deg")))
                .build();
    }

    public WeatherForecastResponse generateResponse(OpenWeatherForecastResponse response) {
        return WeatherForecastResponse.builder()
                .city(response.city.name())
                .country(response.city.country())
                .forecasts(response.list.stream()
                        .map(forecast -> IndividualWeatherForecast.builder()
                                .date(time(forecast.dt(), city.timeZone()))
                                .description(forecast.weather().get(0).get("description"))
                                .conditions(forecast.weather().get(0).get("conditions"))
                                .temperature(forecast.main().get("temp"))
                                .humidity(Float.floatToIntBits(forecast.main().get("humidity")))
                                .windSpeed(Float.floatToIntBits(forecast.wind().get("speed")))
                                .build()
                        ).toList()

                )
                .build();
    }

    LocalDateTime time(long dt, int tZ) {
        return TimeConverter.time(dt, tZ);
    }
}
