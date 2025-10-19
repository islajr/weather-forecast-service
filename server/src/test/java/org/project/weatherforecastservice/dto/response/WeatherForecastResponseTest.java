package org.project.weatherforecastservice.dto.response;

import org.junit.jupiter.api.Test;
import org.project.weatherforecastservice.entity.City;
import org.project.weatherforecastservice.entity.Forecast;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class WeatherForecastResponseTest {

    /* test data builders */
    City generateCity() {
        return City.builder()
                .id(1L)
                .longitude(12.2)
                .latitude(13.3)
                .name("sample_city")
                .country("sample_country")
                .searchCount(0L)
                .lastSearched(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    List<Forecast> generateBareForecasts(City city) {
        return List.of(
                Forecast.builder()
                        .id("1L")
                        .forecastDate(LocalDateTime.of(2020, 1, 1, 0, 0))
                        .weatherCondition("sunny")
                        .weatherDescription("quite hot")
                        .humidity(100)
                        .pressure(100)
                        .temperature(100)
                        .windDirection(130)
                        .windSpeed(200)
                        .sunrise(LocalDateTime.of(2020, 1, 1, 0, 0).minusHours(6))
                        .sunset(LocalDateTime.of(2020, 1, 1, 0, 0).plusHours(6))
                        .createdAt(LocalDateTime.of(2020, 1, 1, 0, 0))
                        .updatedAt(LocalDateTime.of(2020, 1, 1, 0, 0))
                        .city(city)
                        .rainVolume(0L)
                        .probability(50)
                        .build(),
                Forecast.builder()
                        .id("2L")
                        .forecastDate(LocalDateTime.of(2020, 1, 1, 3, 0))
                        .weatherCondition("sunny")
                        .weatherDescription("quite hot")
                        .humidity(100)
                        .pressure(100)
                        .temperature(100)
                        .windDirection(130)
                        .windSpeed(200)
                        .sunrise(LocalDateTime.of(2020, 1, 1, 0, 0).minusHours(6))
                        .sunset(LocalDateTime.of(2020, 1, 1, 0, 0).plusHours(3))
                        .createdAt(LocalDateTime.of(2020, 1, 1, 0, 0))
                        .updatedAt(LocalDateTime.of(2020, 1, 1, 0, 0))
                        .city(city)
                        .rainVolume(0L)
                        .probability(50)
                        .build(),
                Forecast.builder()
                        .id("3L")
                        .forecastDate(LocalDateTime.of(2020, 1, 1, 9, 0))
                        .weatherCondition("sunny")
                        .weatherDescription("quite hot")
                        .humidity(100)
                        .pressure(100)
                        .temperature(100)
                        .windDirection(130)
                        .windSpeed(200)
                        .sunrise(LocalDateTime.of(2020, 1, 1, 0, 0).minusHours(9))
                        .sunset(LocalDateTime.of(2020, 1, 1, 0, 0).plusHours(0))
                        .createdAt(LocalDateTime.of(2020, 1, 1, 0, 0))
                        .updatedAt(LocalDateTime.of(2020, 1, 1, 0, 0))
                        .city(city)
                        .rainVolume(0L)
                        .probability(50)
                        .build()
        );
    }

    WeatherForecastResponse generateWeatherForecastResponse(List<Forecast> forecasts, City city) {
        return WeatherForecastResponse.builder()
                .city(city.getName())
                .forecasts(forecasts.stream()
                        .map(forecast -> IndividualWeatherForecast.builder()
                                .description(forecast.getWeatherDescription())
                                .conditions(forecast.getWeatherCondition())
                                .date(forecast.getForecastDate())
                                .windSpeed(forecast.getWindSpeed())
                                .temperature(forecast.getTemperature())
                                .humidity(forecast.getHumidity())
                                .build())
                        .toList())
                .build();
    }

    @Test
    void testShouldGenerateRawResponse() {

        City city = generateCity();
        List<Forecast> forecasts = generateBareForecasts(city);
        WeatherForecastResponse actual = WeatherForecastResponse.generateRawResponse(forecasts, city);
        WeatherForecastResponse expected = generateWeatherForecastResponse(forecasts, city);

        assertNotNull(actual);
        assertEquals(expected, actual);
    }
}