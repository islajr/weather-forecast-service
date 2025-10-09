package org.project.weatherforecastservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CurrentWeather extends Weather {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne(optional = false)
    @JoinColumn(nullable = false, name = "city_id")
    private City cityId;

    @Column(nullable = false, name = "temperature")
    private double temperature; // deg celsius

    @Column(name = "humidity", nullable = false)
    private int humidity;   // %

    @Column(nullable = false, name = "wind_speed")
    private double windSpeed;   // m/s

    @Column(nullable = false, name = "wind_direction")
    private int windDirection;  // deg

    @Column(nullable = false, name = "pressure")
    private int pressure;   // hPa

    @Column(nullable = false, name = "conditions")
    private String weatherCondition;

    @Column(nullable = false, name = "description")
    private String weatherDescription;

    @Column(nullable = false, name = "sunrise")
    private LocalDateTime sunrise;

    @Column(nullable = false, name = "sunset")
    private LocalDateTime sunset;

    @Column(nullable = false, name = "timestamp")
    private LocalDateTime timestamp;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
