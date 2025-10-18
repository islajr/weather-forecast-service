package org.project.weatherforecastservice.repository;

import org.project.weatherforecastservice.entity.Forecast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ForecastRepository extends JpaRepository<Forecast, String> {
    List<Forecast> findForecastsByCityName(String cityName);

    boolean existsByCityName(String cityName);
}
