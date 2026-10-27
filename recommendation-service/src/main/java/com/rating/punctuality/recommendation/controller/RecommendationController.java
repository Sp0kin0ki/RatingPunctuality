package com.rating.punctuality.recommendation.controller;

import com.rating.punctuality.recommendation.dto.RecommendationResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/internal")
public class RecommendationController {

    @GetMapping("/recommendations/{bookId}")
    @PreAuthorize("hasRole('SERVICE')")
    public RecommendationResponse recommendations(@PathVariable UUID bookId) {
        return new RecommendationResponse(bookId, List.of("Clean Code", "Refactoring"));
    }

    @GetMapping("/admin/info")
    @PreAuthorize("hasRole('OPERATOR')")
    public Map<String, String> info() {
        return Map.of("service", "recommendation-service", "status", "ok");
    }
}
