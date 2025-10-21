package com.rating.punctuality.rating_punctuality.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.rating.punctuality.rating_punctuality.model.internal.AirlineDirection;
import com.rating.punctuality.rating_punctuality.model.internal.AirlinePunctuality;
import com.rating.punctuality.rating_punctuality.model.internal.AirlineRating;
import com.rating.punctuality.rating_punctuality.model.internal.AirlineRatingResponse;
import com.rating.punctuality.rating_punctuality.model.internal.InternalAirport;
import com.rating.punctuality.rating_punctuality.model.internal.CancellationsDistribution;
import com.rating.punctuality.rating_punctuality.repository.internal.AirlineRatingRepository;
import com.rating.punctuality.rating_punctuality.repository.internal.InternalAirportRepository;
import com.rating.punctuality.rating_punctuality.repository.internal.CancellationsDistributionRepository;
import com.rating.punctuality.rating_punctuality.repository.internal.DepartureDelaysRepository;

@RestController
public class InternalController {
    private final AirlineRatingRepository ratingRepository;
    private final InternalAirportRepository airportRepository;
    private final DepartureDelaysRepository delaysRepository;
    private final CancellationsDistributionRepository cancellationsDistributionRepository;
    
    public InternalController(AirlineRatingRepository ratingRepository, InternalAirportRepository airportRepository, DepartureDelaysRepository delaysRepository
    ,CancellationsDistributionRepository cancellationsDistributionRepository) {
        this.ratingRepository = ratingRepository;
        this.airportRepository = airportRepository;
        this.delaysRepository = delaysRepository;
        this.cancellationsDistributionRepository = cancellationsDistributionRepository;
    }

    @GetMapping("/get_top3")
    public List<AirlineRatingResponse> getTopThree() {
        List<AirlineRating> ratings = ratingRepository.findTopAirlines(3);
        
        return ratings.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/get_airports")
    public List<InternalAirport> getAirport() {
        List<InternalAirport> airports = airportRepository.getAllAirports();
        return airports.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/delay_histogram")
    public Object getDepartureDelays() {
        return delaysRepository.getDepartureDelays();
    }

    @GetMapping("/cancellations_distribution")
    public List<CancellationsDistribution> getCancellationsDistribution() {
        List<CancellationsDistribution> cancellationsDistributions = cancellationsDistributionRepository.getCancellationsDistribution();

        return cancellationsDistributions.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    @GetMapping("/get_all_direction")
    public ResponseEntity<?> getAllDirections() {
         try {
            org.springframework.core.io.Resource resource = 
                new ClassPathResource("templates/flight_direction_stats.json");
            
            if (!resource.exists()) {
                return ResponseEntity.status(404).body("File not found in templates");
        }

            ObjectMapper mapper = new ObjectMapper();
            mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
            List<AirlineDirection> directions = mapper.readValue(
                resource.getInputStream(), 
                new TypeReference<List<AirlineDirection>>() {}
            );
            
            return ResponseEntity.ok(directions);
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error reading file: " + e.getMessage());
        }
        
    }

    @GetMapping("/get_airline_punctuality")
    public ResponseEntity<?> getAirlinePunctuality() {
        try {
            org.springframework.core.io.Resource resource = 
                new ClassPathResource("templates/airline_punctuality.json");
            
            if (!resource.exists()) {
                return ResponseEntity.status(404).body("File not found in templates");
        }

            ObjectMapper mapper = new ObjectMapper();
            mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
            List<AirlinePunctuality> directions = mapper.readValue(
                resource.getInputStream(), 
                new TypeReference<List<AirlinePunctuality>>() {}
            );
            
            return ResponseEntity.ok(directions);
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error reading file: " + e.getMessage());
        }
    }

    private AirlineRatingResponse convertToResponse(AirlineRating rating) {
        return new AirlineRatingResponse(
            rating.getAirlineIataCode(),
            rating.getAirlineName(),
            rating.getRatingDeparture(),
            rating.getRatingArrival(),
            formatDateTime(rating.getCreatedAt())
        );
    }

    private InternalAirport convertToResponse(InternalAirport airport) {
        return new InternalAirport(
            airport.getIataCode(),
            airport.getAirportName(),
            airport.getLongitude(),
            airport.getLatitude(),
            airport.getCountDeparture(),
            airport.getCountArrival()
        );
    }

    private CancellationsDistribution convertToResponse(CancellationsDistribution cancellationsDistribution) {
        return new CancellationsDistribution(
            cancellationsDistribution.getAirline(),
            cancellationsDistribution.getCancellations()
        );
    }


    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }
}