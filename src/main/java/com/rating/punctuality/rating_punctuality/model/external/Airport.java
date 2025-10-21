package com.rating.punctuality.rating_punctuality.model.external;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Airport {
    private String iataCode;
    private String airportName;
    private String city;
    private String tineZone;
    private double longitude;
    private double latitude;
}
