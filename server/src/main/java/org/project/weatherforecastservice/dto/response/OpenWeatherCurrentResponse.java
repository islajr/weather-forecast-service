package org.project.weatherforecastservice.dto.response;

import lombok.Builder;
import org.project.weatherforecastservice.entity.City;
import org.project.weatherforecastservice.entity.CurrentWeather;
import org.project.weatherforecastservice.util.TimeConverter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Builder
public record OpenWeatherCurrentResponse(
        Map<String, Double> coord,
        List<Map<String, String>> weather,
        String base,
        Map<String, Float> main,
        int visibility,
        Map<String, Float> wind,
        Map<String, Integer> clouds,
        Long dt,
        Map<String, String> sys,
        int timezone,
        Long id,
        String name,
        int cod
) {

    public City extractCity(OpenWeatherCurrentResponse response) {
        return City.builder()
                .name(response.name)
                .longitude(response.coord.get("lon"))
                .latitude(response.coord.get("lat"))
                .country(response.sys.get("country"))
                .build();
    }

    public CurrentWeather extractCurrentWeather(OpenWeatherCurrentResponse response) {
        return CurrentWeather.builder()
                .temperature(response.main.get("temp"))
                .weatherCondition(response.weather.get(1).get("main"))
                .weatherDescription(response.weather.get(2).get("description"))
                .timestamp(time(response.dt, response.timezone))
                .humidity(Float.floatToIntBits(response.main.get("humidity")))
                .pressure(Float.floatToIntBits(response.main.get("pressure")))
                .sunrise(time(Long.getLong(response.sys.get("sunrise")), response.timezone))
                .sunset(time(Long.getLong(response.sys.get("sunset")), response.timezone))
                .windDirection(Float.floatToIntBits(response.wind.get("direction")))
                .windSpeed(Double.valueOf(response.wind.get("speed")))
                .build();
    }

    LocalDateTime time(Long dt, int tZ) {
        return TimeConverter.time(dt, tZ);
    }

    public CurrentWeatherResponse generateResponse(OpenWeatherCurrentResponse response) {
        return CurrentWeatherResponse.builder()
                .city(response.name)
                .temperature(response.main.get("temp"))
                .conditions(response.weather.get(1).get("main"))
                .description(response.weather.get(2).get("description"))
                .timestamp(time(response.dt, response.timezone))
                .humidity(Float.floatToIntBits(response.main.get("humidity")))
                .pressure(Float.floatToIntBits(response.main.get("pressure")))
                .sunrise(time(Long.getLong(response.sys.get("sunrise")), response.timezone))
                .sunset(time(Long.getLong(response.sys.get("sunset")), response.timezone))
                .windDirection(Float.floatToIntBits(response.wind.get("direction")))
                .windSpeed(Double.valueOf(response.wind.get("speed")))
                .isCached(false)
                .country(response.sys.get("country"))
                .build();
    }
}
