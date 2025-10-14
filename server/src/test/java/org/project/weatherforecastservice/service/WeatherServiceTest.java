package org.project.weatherforecastservice.service;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.weatherforecastservice.dto.response.CurrentWeatherResponse;
import org.project.weatherforecastservice.dto.response.OpenWeatherCurrentResponse;
import org.project.weatherforecastservice.entity.City;
import org.project.weatherforecastservice.entity.CurrentWeather;
import org.project.weatherforecastservice.repository.CityRepository;
import org.project.weatherforecastservice.repository.CurrentWeatherRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    @Mock
    private CurrentWeatherRepository currentWeatherRepository;

    @Mock
    private CityRepository cityRepository;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private WeatherService weatherService;

    /* Test Builders */

    ResponseEntity<OpenWeatherCurrentResponse> generateOpenWeatherCurrentResponse() {
        return ResponseEntity.ok(OpenWeatherCurrentResponse.builder()
                        .coord((Map.of("lon", 7.637, "lat", 45.133)))
                        .weather(List.of(Map.of(
                                "id", "501",
                                "main", "Rain",
                                "description", "Moderate Rain",
                                "icon", "10d")))
                        .base("stations")
                        .main(Map.of(
                                "temp", 284.2F,
                                "feels_like", 282.93F,
                                "temp_min", 283.06F,
                                "temp_max", 286.82F,
                                "pressure", 1021F,
                                "humidity", 60F,
                                "sea_level", 1021F,
                                "grnd_level", 910F))
                        .visibility(10000)
                        .wind(Map.of(
                                "speed", 4.09F,
                                "deg", 121F,
                                "gust", 3.47F
                        ))
                        .clouds(Map.of(
                                "all", 83
                        ))
                        .dt(1726660758L)
                        .sys(Map.of(
                                "type", "1",
                                "id", "6736",
                                "country", "IT",
                                "sunrise", "1726636384",
                                "sunset", "1726680975"
                        ))
                        .timezone(7200)
                        .id(3165523L)
                        .name("Province of Turin")
                        .cod(200)
                .build());
    }

    String baseWeatherURL;
    String city;

    @BeforeEach
    void setUp() {
        baseWeatherURL = "https://api.openweathermap.org/data/2.5/weather?q={city name}&appid={API key}";
        city = "Turin";
    }


    @Test
    void testShouldEnsureThatCurrentWeatherResponseIsReturned() {

        /* initial setup */
        ResponseEntity<OpenWeatherCurrentResponse> thirdPartyResponse = generateOpenWeatherCurrentResponse();
        assertNotNull(thirdPartyResponse.getBody());

        /* mocking scenarios */
        when(restTemplate.getForEntity(baseWeatherURL, OpenWeatherCurrentResponse.class)).thenReturn(thirdPartyResponse);
        when(cityRepository.save(any(City.class))).thenReturn(null);
        when(currentWeatherRepository.save(any(CurrentWeather.class))).thenReturn(null);

        /* testing implementation */
        ResponseEntity<CurrentWeatherResponse> actual =  weatherService.getCurrentWeather(city, request);
        ResponseEntity<CurrentWeatherResponse> expected = ResponseEntity.ok(OpenWeatherCurrentResponse.generateResponse(thirdPartyResponse.getBody()));

        /* assertions and confirmation */
        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertNotNull(actual.getBody());
        assertEquals(actual.getBody(), expected.getBody());
    }

    @Test
    void getWeatherForecast() {
    }
}