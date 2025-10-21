package com.rating.punctuality.rating_punctuality.model.internal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AirlinePunctuality {
    private String code;
    private String airline;
    private double departure;
    private double arrival;
    private double cancel;
    private int countOfFlights;
}
