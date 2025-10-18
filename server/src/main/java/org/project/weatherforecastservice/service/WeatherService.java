package org.project.weatherforecastservice.service;

import java.time.Duration;
import java.time.LocalDateTime;

import org.project.weatherforecastservice.dto.response.CurrentWeatherResponse;
import org.project.weatherforecastservice.dto.response.OpenWeatherCurrentResponse;
import org.project.weatherforecastservice.dto.response.OpenWeatherForecastResponse;
import org.project.weatherforecastservice.dto.response.WeatherForecastResponse;
import org.project.weatherforecastservice.entity.City;
import org.project.weatherforecastservice.entity.CurrentWeather;
import org.project.weatherforecastservice.entity.Forecast;
import org.project.weatherforecastservice.exception.exceptions.CityNotFoundException;
import org.project.weatherforecastservice.exception.exceptions.FailedToGetWeatherException;
import org.project.weatherforecastservice.repository.CityRepository;
import org.project.weatherforecastservice.repository.CurrentWeatherRepository;
import org.project.weatherforecastservice.repository.ForecastRepository;
import static org.project.weatherforecastservice.util.StringConverter.capitalizeString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

    @Value("${third.party.api.weather.current}")
    private String baseWeatherURL;

    @Value("${third.party.api.weather.forecast}")
    private String baseForecastURL;

    @Value("${third.party.api.key}")
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

        String finalURL = baseWeatherURL.replace("{city name}", city).replace("{API key}", apiKey);

        try {
            log.info("Querying third-party API for current weather for city {}", city);
            ResponseEntity<OpenWeatherCurrentResponse> currentResponse = restTemplate.getForEntity(finalURL, OpenWeatherCurrentResponse.class);

            switch (currentResponse.getStatusCode().value()) {

                case 200:
                    log.info("Querying database for possible stored city and weather information");
                    CurrentWeather contextualWeather = currentWeatherRepository.findCurrentWeatherByCityIdName(capitalizeString(city));
                    City contextualCity = cityRepository.findCityByName(capitalizeString(city));

                    if (contextualCity == null) {   // no registered city with said name

                        log.info("No such city found. Creating new city entity to host weather data");
                        assert currentResponse.getBody() != null;
                        contextualCity = currentResponse.getBody().extractCity(currentResponse.getBody());

                        log.info("Persisting new city to db for id generation");
                        log.info("name: {}, id: {}, country: {}, lon: {}, lat: {}", 
                        contextualCity.getName(), contextualCity.getId(), contextualCity.getCountry(), contextualCity.getLongitude(), contextualCity.getLatitude());
                        cityRepository.save(contextualCity);

                        log.info("Returning saved city for further operation");
                        contextualCity = cityRepository.findCityByName(contextualCity.getName());
                    }

                    log.info("Incrementing search count for city - {}", city);
                    contextualCity.incrementSearchCount();

                    log.info("Updating 'lastSearched' attribute for city - {}", city);
                    contextualCity.updateLastSearched(LocalDateTime.now());

                    if (contextualWeather != null) {    // if a current weather entry already exists
                        log.info("Current Weather already exists for contextual city. Performing exchange");
                        contextualWeather = OpenWeatherCurrentResponse.exchange(contextualWeather, currentResponse.getBody());

                    } else {    // otherwise
                        log.info("Creating new Weather Object for city - {}", city);
                        assert currentResponse.getBody() != null;
                        contextualWeather = currentResponse.getBody().extractCurrentWeather(currentResponse.getBody());
                        contextualWeather.setCityId(contextualCity);
                    }

                    log.info("Generating Client Response for city - {}", city);
                    ResponseEntity<CurrentWeatherResponse> response = ResponseEntity.ok(
                            OpenWeatherCurrentResponse.generateResponse(currentResponse.getBody())
                    );

                    log.info("Populating CurrentWeather cache for city - {}", city);
                    currentWeatherCache.put(city, response);

                    log.info("Persisting City and Weather to db");
                    cityRepository.save(contextualCity);
                    currentWeatherRepository.save(contextualWeather);

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

        String finalURL = baseForecastURL.replace("{city name}", capitalizeString(city)).replace("{API key}", apiKey);

        try {
            log.info("Querying third-party API for weather forecast for city {}", city);
            ResponseEntity<OpenWeatherForecastResponse> forecastResponse = restTemplate.getForEntity(finalURL, OpenWeatherForecastResponse.class);

            switch (forecastResponse.getStatusCode().value()) {

                case 200:
                    log.info("Querying database for possible stored city and forecast information");
                    boolean forecastExists  = forecastRepository.existsByCityName(capitalizeString(city));
                    City contextualCity;

                    if (!forecastExists) {  // no forecast exists for provided city

                        log.info("No saved forecast found. Creating new city entity to host weather data");
                        assert forecastResponse.getBody() != null;
                        contextualCity = forecastResponse.getBody().extractCity(forecastResponse.getBody());

                        log.info("Persisting the new city to db for id generation");
                        log.info("name: {}, id: {}, country: {}, lon: {}, lat: {}",
                                contextualCity.getName(), contextualCity.getId(), contextualCity.getCountry(), contextualCity.getLongitude(), contextualCity.getLatitude());
                        cityRepository.save(contextualCity);

                        log.info("Returning just-saved city for further operation");
                        contextualCity = cityRepository.findCityByName(contextualCity.getName());

                    } else {    // forecast exists for provided city name
                        log.info("Obtaining city object from saved forecast");
                        contextualCity = forecastRepository.findFirstByCityName(capitalizeString(city)).getCity();
                    }

                    log.info("Incrementing the search count for city - {}", city);
                    contextualCity.incrementSearchCount();

                    log.info("Updating the 'lastSearched' attribute for city - {}", city);
                    contextualCity.updateLastSearched(LocalDateTime.now());

                    log.info("Creating and saving new forecast objects for city - {}", city);
                    assert forecastResponse.getBody() != null;

                    for (int i = 0; i < forecastResponse.getBody().cnt(); ++i) {
                        Forecast forecast = forecastResponse.getBody().extractForecast(forecastResponse.getBody().list().get(i), forecastResponse.getBody().city());
                        forecast.setCity(contextualCity);
                        forecastRepository.save(forecast);
                    }

                    log.info("Generating a Client Response for city - {}", city);
                    ResponseEntity<WeatherForecastResponse> response = ResponseEntity.ok(
                            OpenWeatherForecastResponse.generateResponse(forecastResponse.getBody())
                    );

                    log.info("Populating WeatherForecast cache for city - {}", city);
                    weatherForecastCache.put(city, response);

                    log.info("Persisting city to db");
                    cityRepository.save(contextualCity);

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
