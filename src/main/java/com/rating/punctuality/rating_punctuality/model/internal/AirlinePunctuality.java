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
public class AirlinePunctuality {
    private String iataCode;
    private String airlineName;
    private int totalFlights;
    private double departurePercentage;
    private double arrivalPercentage;
    private double cancellationPercentage;
}
