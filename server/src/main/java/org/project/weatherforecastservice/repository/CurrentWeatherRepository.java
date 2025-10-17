package org.project.weatherforecastservice.repository;

import org.project.weatherforecastservice.entity.CurrentWeather;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrentWeatherRepository extends JpaRepository<CurrentWeather, String> {

    CurrentWeather findCurrentWeatherByCityIdName(String cityIdName);
}
