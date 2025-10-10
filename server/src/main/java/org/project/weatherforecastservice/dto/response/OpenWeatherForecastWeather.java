package org.project.weatherforecastservice.dto.response;

import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record OpenWeatherForecastWeather(
        long dt,
        Map<String, Float> main,
        List<Map<String, String>> weather,
        Map<String, Integer> clouds,
        Map<String, Float> wind,
        int visibility,
        int pop,
        Map<String, String> sys,
        String dt_txt
) {
}
