package com.rating.punctuality.rating_punctuality.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rating.punctuality.rating_punctuality.model.external.DelayStats;
import com.rating.punctuality.rating_punctuality.model.internal.AirlineRating;
import com.rating.punctuality.rating_punctuality.model.internal.AirlineRatingResponse;
import com.rating.punctuality.rating_punctuality.model.external.Airport;
import com.rating.punctuality.rating_punctuality.repository.external.AirportRepository;
import com.rating.punctuality.rating_punctuality.repository.external.AirportStatRepository;
import com.rating.punctuality.rating_punctuality.repository.external.DelayStatsRepository;
import com.rating.punctuality.rating_punctuality.repository.internal.AirlineRatingRepository;

@RestController
public class ExternalController {
    private final AirlineRatingRepository airlineRating;
    private final AirportStatRepository airportStatRepository;
    private final DelayStatsRepository delayStatsRepository;
    private final AirportRepository airportRepository;

    public ExternalController(AirlineRatingRepository airlineRating, AirportStatRepository airportStatRepository,
            DelayStatsRepository delayStatsRepository, AirportRepository airportRepository) {
        this.airlineRating = airlineRating;
        this.airportStatRepository = airportStatRepository;
        this.delayStatsRepository = delayStatsRepository;
        this.airportRepository = airportRepository;
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

    @GetMapping("/airports/{iataCode}/stats")
    public Object getStats(
        @PathVariable("iataCode") String iataCode
    ) {
        return airportStatRepository.getStats(iataCode);
    }

    @GetMapping("/airlines/{iataCode}/delay-stats")
    public List<DelayStats> getDelayStats(
        @PathVariable("iataCode") String iataCode
    ) {
        List<DelayStats> delayStats = delayStatsRepository.getDelayStats(iataCode);

        return delayStats;
    }

    @GetMapping("/airports")
    public List<Airport> getAirports(
        @RequestParam(defaultValue = "default") String city
    ){
        return airportRepository.getAirports(city);
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
