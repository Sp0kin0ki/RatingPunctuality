package com.rating.punctuality.rating_punctuality.model.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "flights")
@Data
public class Flight {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "iata_code")
    private String iataCode;
    
    private String flight;
    
    @Column(name = "departure_airport")
    private String departureAirport;
    
    @Column(name = "arrival_airport")
    private String arrivalAirport;
    
    @Column(name = "plan_departure")
    private LocalDateTime planDeparture;
    
    @Column(name = "plan_arrival")
    private LocalDateTime planArrival;
    
    @Column(name = "fact_departure")
    private LocalDateTime factDeparture;
    
    @Column(name = "fact_arrival")
    private LocalDateTime factArrival;
}