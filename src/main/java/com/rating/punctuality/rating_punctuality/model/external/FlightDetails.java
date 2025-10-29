package com.rating.punctuality.rating_punctuality.model.external;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class FlightDetails {
    private int id;
    private String iataCode;
    private String flight;
    private String departureAirport;
    private String arrivalAirport;
    private LocalDateTime planDeparture;
    private LocalDateTime planArrival;
    private LocalDateTime factDeparture;
    private LocalDateTime factArrival;
    private String departureAirportName;
    private String departureCity;
    private String arrivalAirportName;
    private String arrivalCity;
    private String dayOfWeek;
    private String timeOfDate;
    private String season;
    private String delayCategory;
}
