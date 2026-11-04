package edu.rutmiit.enterprise.library.controller;

import edu.rutmiit.enterprise.library.entity.Flight;
import edu.rutmiit.enterprise.library.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/flights")
@RequiredArgsConstructor
public class FlightController {

    private static final Logger log = LoggerFactory.getLogger(FlightController.class);
    
    private final FlightRepository flightRepository;
    private final RestClient recommendationRestClient;

    @GetMapping
    public List<Flight> getAllFlights() {
        log.info("Fetching all flights");
        // Моковые данные для демонстрации
        return List.of(
            createMockFlight(1L, "SU123", "SVO", "LED", "SCHEDULED"),
            createMockFlight(2L, "SU456", "LED", "SVO", "DEPARTED"),
            createMockFlight(3L, "SU789", "DME", "LED", "SCHEDULED")
        );
    }
    
    @GetMapping("/{id}/recommendations")
    public Map<String, Object> getFlightWithRecommendations(@PathVariable Long id) {
        log.info("Fetching flight {} with recommendations", id);
        
        Flight flight = createMockFlight(id, "SU" + id, "SVO", "LED", "SCHEDULED");
        
        // Вызов сервиса рекомендаций
        UUID flightUuid = UUID.nameUUIDFromBytes(id.toString().getBytes());
        Map<String, Object> recommendations = recommendationRestClient.get()
                .uri("/internal/recommendations/{flightId}", flightUuid)
                .retrieve()
                .body(Map.class);
        
        log.info("Received recommendations for flight {}", id);
        
        return Map.of(
            "flight", flight,
            "recommendations", recommendations != null ? recommendations : Map.of()
        );
    }
    
    private Flight createMockFlight(Long id, String number, String from, String to, String status) {
        Flight flight = new Flight();
        flight.setId(id);
        flight.setFlightNumber(number);
        flight.setStatus(status);
        flight.setDelayMinutes(0);
        return flight;
    }

    @GetMapping("/{id}")
    public Flight getFlightById(@PathVariable Long id) {
        log.info("Fetching flight {}", id);
        return createMockFlight(id, "SU" + id, "SVO", "LED", "SCHEDULED");
    }

    @PostMapping
    @PreAuthorize("hasRole('EDITOR')")
    @ResponseStatus(HttpStatus.CREATED)
    public Flight createFlight(@RequestBody Flight flight) {
        log.info("Creating flight: {}", flight.getFlightNumber());
        flight.setId(999L);
        return flight;
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('EDITOR')")
    public Flight updateFlight(@PathVariable Long id, @RequestBody Flight updatedFlight) {
        log.info("Updating flight {}", id);
        return createMockFlight(id, updatedFlight.getFlightNumber(), "SVO", "LED", updatedFlight.getStatus());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('EDITOR')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFlight(@PathVariable Long id) {
        log.info("Deleting flight {}", id);
    }
}
