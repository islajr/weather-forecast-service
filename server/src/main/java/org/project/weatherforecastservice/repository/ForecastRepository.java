package org.project.weatherforecastservice.repository;

import org.project.weatherforecastservice.entity.Forecast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForecastRepository extends JpaRepository<Forecast, String> {
}
