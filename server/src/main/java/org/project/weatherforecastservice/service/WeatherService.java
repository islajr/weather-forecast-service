package org.project.weatherforecastservice.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.weatherforecastservice.dto.response.CurrentWeatherResponse;
import org.project.weatherforecastservice.dto.response.OpenWeatherCurrentResponse;
import org.project.weatherforecastservice.dto.response.WeatherForecastResponse;
import org.project.weatherforecastservice.entity.City;
import org.project.weatherforecastservice.entity.CurrentWeather;
import org.project.weatherforecastservice.exception.exceptions.CityNotFoundException;
import org.project.weatherforecastservice.exception.exceptions.FailedToGetWeatherException;
import org.project.weatherforecastservice.repository.CityRepository;
import org.project.weatherforecastservice.repository.CurrentWeatherRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDateTime;

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
    private final RestTemplate restTemplate;

    /* Caffeine Caches */
    Cache<String, ResponseEntity<CurrentWeatherResponse>> currentWeatherCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(Duration.ofMinutes(15))
            .build();

    Cache<String, ResponseEntity<CurrentWeatherResponse>> weatherForecastCache = Caffeine.newBuilder()
            .maximumSize(5000)
            .expireAfterWrite(Duration.ofHours(1))
            .build();

    Cache<String, ResponseEntity<CurrentWeatherResponse>> citySearchCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(Duration.ofDays(1))
            .build();

    public ResponseEntity<CurrentWeatherResponse> getCurrentWeather(String city, HttpServletRequest request) {

        // return cached response originally
        ResponseEntity<CurrentWeatherResponse> cachedResponse = currentWeatherCache.getIfPresent(city);
        if (cachedResponse != null) {
            return cachedResponse;
        }

        baseWeatherURL = baseWeatherURL.replace("{city}", city).replace("{apikey}", apiKey);

        try {
            log.info("Querying third-party API for current weather for city {}", city);
            ResponseEntity<OpenWeatherCurrentResponse> currentResponse = restTemplate.getForEntity(baseWeatherURL, OpenWeatherCurrentResponse.class);

            switch (currentResponse.getStatusCode().toString()) {

                case "200":
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
                            currentResponse.getBody().generateResponse(currentResponse.getBody())
                    );

                    log.info("Populating CurrentWeather cache for city - {}", city);
                    currentWeatherCache.put(city, response);

                    log.info("Persisting city and Weather to db");
                    cityRepository.save(contextualCity);
                    currentWeatherRepository.save(weather);

                    return response;

                case "502":
                    throw new FailedToGetWeatherException("Failed to get current weather for city: " + city);

                case "404":
                    throw new CityNotFoundException("Could not find City: " + city);

                default:
                    throw new RuntimeException("Failed to get current weather for city: " + city);
            }

        } catch(HttpClientErrorException | HttpServerErrorException ex) {
            throw new RuntimeException("Something went wrong!");
        }
    }

    public ResponseEntity<WeatherForecastResponse> getWeatherForecast(String city, HttpServletRequest request) {
        return null;
    }
}
