package com.rating.punctuality.rating_punctuality.model.external;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Flight {
    private Long id;
    private String iataCode;
    private String flight;
    private String departureAirport;
    private String arrivalAirport;
    private LocalDateTime planDeparture;
    private LocalDateTime planArrival;
    private LocalDateTime factDeparture;
    private LocalDateTime factArrival;
    private String dayOfWeek;
    private String timeOfDay;
    private String season;
    private String delayCategory;
}