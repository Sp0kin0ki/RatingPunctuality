package com.rating.punctuality.recommendation.dto;

import java.util.List;
import java.util.UUID;

public record RecommendationResponse(UUID bookId, List<String> titles) {
}
