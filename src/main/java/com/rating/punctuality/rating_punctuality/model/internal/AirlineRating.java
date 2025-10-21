package com.rating.punctuality.rating_punctuality.model.internal;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AirlineRating {
    private String airlineIataCode;
    private String airlineName;
    private Double ratingDeparture;
    private Double ratingArrival;
    private LocalDateTime createdAt;
}