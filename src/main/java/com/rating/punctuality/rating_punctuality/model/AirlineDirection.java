package com.rating.punctuality.rating_punctuality.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AirlineDirection {
    private String airport1;
    private String airport2;
    private int totalFlights;
    private int onTimeArrivals;
    private double onTimePercentage;
    private double avgDelayMinutes;
    private int missingDepartureCount;

}
