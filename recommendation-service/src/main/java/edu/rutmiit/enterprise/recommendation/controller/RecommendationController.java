package edu.rutmiit.enterprise.recommendation.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/internal/recommendations")
public class RecommendationController {

    private static final Logger log = LoggerFactory.getLogger(RecommendationController.class);

    @GetMapping("/{flightId}")
    public Map<String, Object> getRecommendations(@PathVariable UUID flightId) {
        log.info("Generating recommendations for flight: {}", flightId);
        
        // Моковые рекомендации рейсов
        List<String> recommendations = List.of(
            "SU123 Moscow-Petersburg",
            "SU456 Petersburg-Moscow",
            "SU789 Moscow-Sochi"
        );
        
        return Map.of(
            "flightId", flightId,
            "recommendations", recommendations
        );
    }
}
