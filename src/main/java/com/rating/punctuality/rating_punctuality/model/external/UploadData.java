package com.rating.punctuality.rating_punctuality.model.external;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "flights")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "iata_code", nullable = false)
    private String iataCode;

    @Column(name = "flight", nullable = false)
    private String flight;

    @Column(name = "departure_airport", nullable = false)
    private String departureAirport;
    
    @Column(name = "arrival_airport", nullable = false)
    private String arrivalAirport;

    @Column(name = "plan_departure", nullable = false)
    private LocalDateTime planDeparture;

    @Column(name = "plan_arrival", nullable = false)
    private LocalDateTime planArrival;

    @Column(name = "fact_departure", nullable = false)
    private LocalDateTime factDeparture;

    @Column(name = "fact_arrival", nullable = false)
    private LocalDateTime factArrival;

}
