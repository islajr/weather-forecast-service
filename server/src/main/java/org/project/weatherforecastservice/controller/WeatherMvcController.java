package org.project.weatherforecastservice.controller;

import java.util.Objects;

import org.project.weatherforecastservice.service.WeatherService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class WeatherMvcController {

    private final WeatherService weatherService;

    @GetMapping("/")
    public String displayHome() {
        return "home";
    }

    @GetMapping("/weather")
    public String getCurrentWeather(@RequestParam("city") String city, Model model, HttpServletRequest request) {

        model.addAttribute("city", city);
        model.addAttribute("response", Objects.requireNonNull(weatherService.getCurrentWeather(city, request).getBody()));
        return "weather";
    }

    @GetMapping("/forecast")
    public String getForecast(@RequestParam("city") String city, Model model, HttpServletRequest request) {
        model.addAttribute("city", city);
        model.addAttribute("response", Objects.requireNonNull(weatherService.getWeatherForecast(city, request).getBody()));

        return "weather";
    }
}
