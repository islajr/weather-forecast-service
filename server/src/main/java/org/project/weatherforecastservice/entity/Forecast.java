package org.project.weatherforecastservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Forecast extends Weather {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false, name = "city_id")
    private City cityId;

    @Column(nullable = false, name = "forecast_date")
    private LocalDate forecastDate;

    @Column(nullable = false, name = "rain_volume")
    private double rainVolume;

    @Column(nullable = false, name = "probability")
    private float probability;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
