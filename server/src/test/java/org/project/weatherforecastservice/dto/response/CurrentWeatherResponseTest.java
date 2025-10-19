package org.project.weatherforecastservice.dto.response;

import org.junit.jupiter.api.Test;
import org.project.weatherforecastservice.entity.City;
import org.project.weatherforecastservice.entity.CurrentWeather;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CurrentWeatherResponseTest {

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
                .createdAt(LocalDateTime.of(2020, 1, 1, 0, 0))
                .updatedAt(LocalDateTime.of(2020, 1, 1, 0, 0))
                .build();
    }

    CurrentWeather generateCurrentWeather(City city) {
        return CurrentWeather.builder()
                .id("#1")
                .temperature(1.25)
                .cityId(city)
                .pressure(100)
                .timestamp(LocalDateTime.of(2020, 1, 1, 0, 0))
                .humidity(150)
                .weatherDescription("quite hot")
                .weatherCondition("sunny")
                .windSpeed(120)
                .windDirection(150)
                .sunrise(LocalDateTime.of(2020, 1, 1, 0, 0).minusHours(6))
                .sunset(LocalDateTime.of(2020, 1, 1, 0, 0).plusHours(6))
                .build();
    }

    CurrentWeatherResponse generateTestResponse(City city) {
        return CurrentWeatherResponse.builder()
                .country("sample_country")
                .city(city.getName())
                .windDirection(150)
                .windSpeed(120)
                .sunrise(LocalDateTime.of(2020, 1, 1, 0 , 0).minusHours(6))
                .sunset(LocalDateTime.of(2020,1, 1, 0, 0).plusHours(6))
                .humidity(150)
                .pressure(100)
                .timestamp(LocalDateTime.of(2020, 1, 1, 0, 0))
                .temperature(1.25)
                .description("quite hot")
                .conditions("sunny")
                .build();
    }

    @Test
    void testShouldGenerateRawResponse() {
        City city = generateCity();
        CurrentWeather currentWeather = generateCurrentWeather(city);
        CurrentWeatherResponse expected = generateTestResponse(city);
        CurrentWeatherResponse actual = CurrentWeatherResponse.generateRawResponse(currentWeather, city);

        assertNotNull(actual);
        assertEquals(expected, actual);
    }
}