package com.rating.punctuality.rating_punctuality.model.internal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AirlineDirection {
    private String airportFirst;
    private String airportSecond;
    private int totalFlights;
    private int onTimeArrivals;
    private double onTimePercentage;
    private double avgDelayMinutes;
    private int missingDepartureCount;

}
