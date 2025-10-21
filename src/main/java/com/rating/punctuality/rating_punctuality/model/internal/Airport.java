package com.rating.punctuality.rating_punctuality.model.internal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Airport {
    private String iataCode;
    private String airportName;
    private double longitude;
    private double latitude;
    private int countDeparture;
    private int countArrival;
}
