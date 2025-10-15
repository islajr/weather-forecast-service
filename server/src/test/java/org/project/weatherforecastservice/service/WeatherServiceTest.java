package org.project.weatherforecastservice.service;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.weatherforecastservice.dto.response.*;
import org.project.weatherforecastservice.entity.City;
import org.project.weatherforecastservice.entity.CurrentWeather;
import org.project.weatherforecastservice.repository.CityRepository;
import org.project.weatherforecastservice.repository.CurrentWeatherRepository;
import org.project.weatherforecastservice.repository.ForecastRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    @Mock
    private CurrentWeatherRepository currentWeatherRepository;

    @Mock
    private CityRepository cityRepository;

    @Mock
    private ForecastRepository forecastRepository;

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

    ResponseEntity<OpenWeatherForecastResponse> generateOpenWeatherForecastResponse() {
        return ResponseEntity.ok(OpenWeatherForecastResponse.builder()
                        .cod("200")
                        .message(0)
                        .cnt(3)
                        .list(List.of(
                                OpenWeatherForecastWeather.builder()
                                        .dt(1647345600)
                                        .main(Map.of(
                                                "temp", 274.05F,
                                                "feels_like", 270.56F,
                                                "temp_min", 272.67F,
                                                "temp_max", 274.05F,
                                                "pressure", 1035F,
                                                "sea_level", 1035F,
                                                "grnd_level", 1016F,
                                                "humidity", 56F,
                                                "temp_kf", 1.38F
                                        ))
                                        .weather(List.of(
                                                Map.of(
                                                        "id", "800",
                                                        "main", "Clear",
                                                        "description", "clear sky",
                                                        "icon", "01d"
                                                )
                                        ))
                                        .clouds(Map.of(
                                                "all", 8
                                        ))
                                        .wind(Map.of(
                                                "speed", 3.2F,
                                                "deg", 28F,
                                                "gust", 4.62F
                                        ))
                                        .visibility(10000)
                                        .pop(0)
                                        .sys(Map.of(
                                                "pod", "d"
                                        ))
                                        .dt_txt("2022-03-15 12:00:00")
                                .build(),
                                OpenWeatherForecastWeather.builder()
                                        .dt(1647356400)
                                        .main(Map.of(
                                                "temp", 272.74F,
                                                "feels_like", 270.19F,
                                                "temp_min", 270.12F,
                                                "temp_max", 272.74F,
                                                "pressure", 1035F,
                                                "sea_level", 1035F,
                                                "grnd_level", 1016F,
                                                "humidity", 60F,
                                                "temp_kf", 2.62F
                                        ))
                                        .weather(List.of(
                                                Map.of(
                                                        "id", "801",
                                                        "main", "Clouds",
                                                        "description", "few clouds",
                                                        "icon", "02d"
                                                )
                                        ))
                                        .clouds(Map.of(
                                                "all", 23
                                        ))
                                        .wind(Map.of(
                                                "speed", 2.02F,
                                                "deg", 46F,
                                                "gust", 4.27F
                                        ))
                                        .visibility(10000)
                                        .pop(0)
                                        .sys(Map.of(
                                                "pod", "d"
                                        ))
                                        .dt_txt("2022-03-15 15:00:00")
                                .build(),
                                OpenWeatherForecastWeather.builder()
                                        .dt(1647367200)
                                        .main(Map.of(
                                                "temp", 269.89F,
                                                "feels_like", 266.36F,
                                                "temp_min", 267.81F,
                                                "temp_max", 269.89F,
                                                "pressure", 1036F,
                                                "sea_level", 1036F,
                                                "grnd_level", 1017F,
                                                "humidity", 78F,
                                                "temp_kf", 2.08F
                                        ))
                                        .weather(List.of(
                                                Map.of(
                                                        "id", "802",
                                                        "main", "Clouds",
                                                        "description", "scattered clouds",
                                                        "icon", "03d"
                                                )
                                        ))
                                        .clouds(Map.of(
                                                "all", 33
                                        ))
                                        .wind(Map.of(
                                                "speed", 2.42F,
                                                "deg", 58F,
                                                "gust", 7.57F
                                        ))
                                        .visibility(10000)
                                        .pop(0)
                                        .sys(Map.of(
                                                "pod", "n"
                                        ))
                                        .dt_txt("2022-03-15 18:00:00")
                                        .build()
                        ))
                        .city(OpenWeatherForecastCity.builder()
                                .id(524901L)
                                .name("Moscow")
                                .coord(Map.of(
                                        "lon", 55.7522,
                                        "lat", 37.6156
                                ))
                                .country("RU")
                                .population(0)
                                .timeZone(10800)
                                .sunrise(1647315936)
                                .sunset(1647358295)
                        .build())
                .build());
                
    }

    String baseWeatherURL;
    String baseForecastURL;
    String city;

    @BeforeEach
    void setUp() {
        baseWeatherURL = "https://api.openweathermap.org/data/2.5/weather?q={city name}&appid={API key}";
        baseForecastURL = "https://api.openweathermap.org/data/2.5/forecast?q={city name}&appid={API key}";
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

        /* initial setup*/
        city = "Moscow";
        ResponseEntity<OpenWeatherForecastResponse> thirdPartyResponse = generateOpenWeatherForecastResponse();
        assertNotNull(thirdPartyResponse.getBody());

        /* mocking scenarios */
        when(restTemplate.getForEntity(baseForecastURL, OpenWeatherForecastResponse.class)).thenReturn(thirdPartyResponse);
        when(cityRepository.save(any(City.class))).thenReturn(null);
        when(forecastRepository.saveAll(any())).thenReturn(null);

        /* testing implementation */
        ResponseEntity<WeatherForecastResponse> actual =  weatherService.getWeatherForecast(city, request);
        ResponseEntity<WeatherForecastResponse> expected = ResponseEntity.ok(OpenWeatherForecastResponse.generateResponse(thirdPartyResponse.getBody()));

        /* assertions and verification */
        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertNotNull(actual.getBody());
        assertEquals(actual.getBody(), expected.getBody());

    }
}