package com.rating.punctuality.rating_punctuality.model.internal;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CalculateFlightDirection {
    private String airportFirst;
    private String airportSecond;
    private int totalFlights;
    private int onTimeArrivals;
    private double onTimePercentage;
    private double avgDelayMinutes;
    private int missingDepartureCount;
}
