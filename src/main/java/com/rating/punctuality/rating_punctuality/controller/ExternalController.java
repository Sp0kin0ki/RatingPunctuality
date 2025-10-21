package com.rating.punctuality.rating_punctuality.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rating.punctuality.rating_punctuality.model.internal.AirlineRating;
import com.rating.punctuality.rating_punctuality.model.internal.AirlineRatingResponse;
import com.rating.punctuality.rating_punctuality.repository.AirlineRatingRepository;

@RestController
public class ExternalController {
    private final AirlineRatingRepository airlineRating;

    
    public ExternalController(AirlineRatingRepository airlineRating) {
        this.airlineRating = airlineRating;
    }


    @GetMapping("/airlines/top")
    public List<AirlineRatingResponse> getTopAirlines(
        @RequestParam(defaultValue = "3") int limit
    ) {
        List<AirlineRating> ratings = airlineRating.findTopAirlines(limit);
        System.out.println(limit);
        return ratings.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private AirlineRatingResponse convertToResponse(AirlineRating rating) {
        return new AirlineRatingResponse(
            rating.getAirlineIataCode(),
            rating.getAirlineName(),
            rating.getRatingDeparture(),
            rating.getRatingArrival(),
            InternalController.formatDateTime(rating.getCreatedAt())
        );
    }
}
