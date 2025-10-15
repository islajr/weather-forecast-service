package org.project.weatherforecastservice.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.weatherforecastservice.dto.response.*;
import org.project.weatherforecastservice.entity.*;
import org.project.weatherforecastservice.exception.exceptions.*;
import org.project.weatherforecastservice.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherService {

    /* *
     * Generate OpenWeatherMap API key
     * Query the third-party api for weather (current/forecast)
     * Convert response to the appropriate data entity
     * Persist to the db
     * Update the necessary tables and lists
     * Cache the response
     * Return the response [REST or SSG Document]
     * */

    @Value("${openweathermap.api.weather.city.current}")
    private String baseWeatherURL;

    @Value("${openweathermap.api.weather.city.forecast}")
    private String baseForecastURL;

    @Value("${openweathermap.api.key}")
    private String apiKey;

    private final CityRepository cityRepository;
    private final CurrentWeatherRepository currentWeatherRepository;
    private final ForecastRepository forecastRepository;
    private final RestTemplate restTemplate;

    /* Caffeine Caches */
    Cache<String, ResponseEntity<CurrentWeatherResponse>> currentWeatherCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(Duration.ofMinutes(15))
            .build();

    Cache<String, ResponseEntity<WeatherForecastResponse>> weatherForecastCache = Caffeine.newBuilder()
            .maximumSize(5000)
            .expireAfterWrite(Duration.ofHours(1))
            .build();

    public ResponseEntity<CurrentWeatherResponse> getCurrentWeather(String city, HttpServletRequest request) {

        // return cached response originally
        ResponseEntity<CurrentWeatherResponse> cachedResponse = currentWeatherCache.getIfPresent(city);
        if (cachedResponse != null) {
            log.info("Weather Cache Hit: Returning cached value for city - {}", city);
            return cachedResponse;
        }

        baseWeatherURL = baseWeatherURL.replace("{city}", city).replace("{apikey}", apiKey);

        try {
            log.info("Querying third-party API for current weather for city {}", city);
            ResponseEntity<OpenWeatherCurrentResponse> currentResponse = restTemplate.getForEntity(baseWeatherURL, OpenWeatherCurrentResponse.class);

            switch (currentResponse.getStatusCode().value()) {

                case 200:
                    City contextualCity = cityRepository.findCityByName(city).orElseGet(
                            () -> {
                                log.info("City Not Found: Creating new city with name - {}", city);
                                assert currentResponse.getBody() != null;
                                return currentResponse.getBody().extractCity(currentResponse.getBody());
                            }
                    );

                    log.info("Incrementing search count for city - {}", city);
                    contextualCity.incrementSearchCount();

                    log.info("Updating 'lastSearched' attribute for city - {}", city);
                    contextualCity.updateLastSearched(LocalDateTime.now());

                    log.info("Creating new Weather Object for city - {}", city);
                    assert currentResponse.getBody() != null;
                    CurrentWeather weather = currentResponse.getBody().extractCurrentWeather(currentResponse.getBody());
                    weather.setCityId(contextualCity);

                    log.info("Generating Client Response for city - {}", city);
                    ResponseEntity<CurrentWeatherResponse> response = ResponseEntity.ok(
                            OpenWeatherCurrentResponse.generateResponse(currentResponse.getBody())
                    );

                    log.info("Populating CurrentWeather cache for city - {}", city);
                    currentWeatherCache.put(city, response);

                    log.info("Persisting city and Weather to db");
                    cityRepository.save(contextualCity);
                    currentWeatherRepository.save(weather);

                    return response;

                case 502:
                    throw new FailedToGetWeatherException("Failed to get current weather for city: " + city);

                case 404:
                    throw new CityNotFoundException("Could not find City: " + city);

                default:
                    throw new RuntimeException("Failed to get current weather for city: " + city);
            }

        } catch(HttpClientErrorException | HttpServerErrorException ex) {
            throw new RuntimeException("Something went wrong!");
        }
    }

    public ResponseEntity<WeatherForecastResponse> getWeatherForecast(String city, HttpServletRequest request) {

        // return cached response originally
        ResponseEntity<WeatherForecastResponse> cachedResponse = weatherForecastCache.getIfPresent(city);
        if (cachedResponse != null) {
            log.info("Forecast Cache Hit: Returning cached value for city - {}", city);
            return cachedResponse;
        }

        baseForecastURL = baseForecastURL.replace("{city}", city).replace("{apikey}", apiKey);

        try {
            log.info("Querying third-party API for weather forecast for city {}", city);
            ResponseEntity<OpenWeatherForecastResponse> forecastResponse = restTemplate.getForEntity(baseForecastURL, OpenWeatherForecastResponse.class);

            switch (forecastResponse.getStatusCode().value()) {

                case 200:
                    City contextualCity = cityRepository.findCityByName(city).orElseGet(
                            () -> {
                                log.info("City not found: Creating new city with name - {}", city);
                                assert forecastResponse.getBody() != null;
                                return forecastResponse.getBody().extractCity(forecastResponse.getBody());
                            }
                    );

                    log.info("Incrementing the search count for city - {}", city);
                    contextualCity.incrementSearchCount();

                    log.info("Updating the 'lastSearched' attribute for city - {}", city);
                    contextualCity.updateLastSearched(LocalDateTime.now());

                    log.info("Creating a new Weather Object for city - {}", city);
                    assert forecastResponse.getBody() != null;

                    List<Forecast> littleForecast = new ArrayList<>();
                    for (int i = 0; i < forecastResponse.getBody().cnt(); ++i) {
                        Forecast forecast = forecastResponse.getBody().extractForecast(forecastResponse.getBody().list().get(i), forecastResponse.getBody().city());
                        forecast.setCityId(contextualCity);
                        littleForecast.add(forecast);
                    }

                    log.info("Generating a Client Response for city - {}", city);
                    ResponseEntity<WeatherForecastResponse> response = ResponseEntity.ok(
                            OpenWeatherForecastResponse.generateResponse(forecastResponse.getBody())
                    );

                    log.info("Populating WeatherForecast cache for city - {}", city);
                    weatherForecastCache.put(city, response);

                    log.info("Persisting city and Forecast to db");
                    cityRepository.save(contextualCity);
                    forecastRepository.saveAll(littleForecast);
                    littleForecast.clear();

                    return response;

                case 502:
                    throw new FailedToGetWeatherException("Failed to get current weather for city: " + city);

                case 404:
                    throw new CityNotFoundException("Could not find City: " + city);

                default:
                    throw new RuntimeException("Failed to get current weather for city: " + city);
            }

        } catch(HttpClientErrorException | HttpServerErrorException ex) {
            throw new RuntimeException("Something went wrong!");
        }
    }
}
