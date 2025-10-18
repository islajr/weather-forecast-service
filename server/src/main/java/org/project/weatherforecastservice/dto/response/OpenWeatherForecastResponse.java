package org.project.weatherforecastservice.dto.response;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.project.weatherforecastservice.entity.City;
import org.project.weatherforecastservice.entity.Forecast;
import org.project.weatherforecastservice.util.TimeConverter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
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
                .searchCount(0L)
                .lastSearched(null)
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

    public List<Forecast> exchange(OpenWeatherForecastResponse response, List<Forecast> oldForecast) {
        List<Forecast> newForecast = new ArrayList<>();

        // populating the new forecasts with existing conversion implementation
        for (OpenWeatherForecastWeather weather : response.list) {
            newForecast.add(extractForecast(weather, response.city));
        }

        // swapping out the values and fallback conditions
        if (newForecast.size() == oldForecast.size()) {
            for (int i = 0; i < newForecast.size(); i++) {
                oldForecast.set(i, newForecast.get(i));
            }
            log.info("Successfully swapped out old forecast data");
        } else {
            log.warn("Error exchanging forecasts: new and old forecasts are of varying length");
            throw new RuntimeException("Failed to exchange forecasts - Varying length Exception");
        }

        return oldForecast;
    }

    public static WeatherForecastResponse generateResponse(OpenWeatherForecastResponse response) {
        return WeatherForecastResponse.builder()
                .city(response.city.name())
                .country(response.city.country())
                .forecasts(response.list.stream()
                        .map(forecast -> IndividualWeatherForecast.builder()
                                .date(time(forecast.dt(), response.city.timeZone()))
                                .description(forecast.weather().get(0).get("description"))
                                .conditions(forecast.weather().get(0).get("main"))
                                .temperature(forecast.main().get("temp"))
                                .humidity(Float.floatToIntBits(forecast.main().get("humidity")))
                                .windSpeed(Float.floatToIntBits(forecast.wind().get("speed")))
                                .build()
                        ).toList()

                )
                .build();
    }

    static LocalDateTime time(long dt, int tZ) {
        return TimeConverter.time(dt, tZ);
    }
}
