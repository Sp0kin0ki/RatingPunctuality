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
public class InternalAirport {
    private String iataCode;
    private String airportName;
    private double longitude;
    private double latitude;
    private int countDeparture;
    private int countArrival;
}
