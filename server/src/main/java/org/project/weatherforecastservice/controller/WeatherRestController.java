package org.project.weatherforecastservice.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.project.weatherforecastservice.dto.response.CurrentWeatherResponse;
import org.project.weatherforecastservice.dto.response.WeatherForecastResponse;
import org.project.weatherforecastservice.service.WeatherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
public class WeatherRestController {

    private final WeatherService weatherService;

    @GetMapping("/current")
    public ResponseEntity<CurrentWeatherResponse> getCurrentWeather(@RequestParam String city, HttpServletRequest request) {
        return weatherService.getCurrentWeather(city, request);
    }

    @GetMapping("/forecast")
    public ResponseEntity<WeatherForecastResponse> getWeatherForecast(@RequestParam String city, HttpServletRequest request) {
        return weatherService.getWeatherForecast(city, request);
    }

}
