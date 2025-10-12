package org.project.weatherforecastservice.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WeatherTasks {

    /* task to refresh the frequently searched city cache */
    @Scheduled()
    private void updateFrequentlySearchedCities() {

    }

    /* basic task for refreshing current weather of most-searched cities - (top five) */
    @Scheduled()
    private void refreshCurrentWeather() {
        
    }
}
