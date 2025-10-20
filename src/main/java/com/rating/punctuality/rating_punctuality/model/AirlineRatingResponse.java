package com.rating.punctuality.rating_punctuality.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AirlineRatingResponse {
    private String airlineIataCode;
    private String airlineName;
    private Double ratingDeparture;
    private Double ratingArrival;
    private String createdAt;
}
