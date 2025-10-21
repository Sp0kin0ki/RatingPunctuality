package com.rating.punctuality.rating_punctuality.repository.internal;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.rating.punctuality.rating_punctuality.model.internal.AirlineRating;

@Repository
public class AirlineRatingRepository {
    
    private final JdbcTemplate jdbcTemplate;
    
    public AirlineRatingRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    public List<AirlineRating> findTopAirlines(int limit) {
        String sql = """
            WITH latest_ratings AS (
                SELECT DISTINCT ON (airline_iata_code) 
                    airline_iata_code, rating_departure, rating_arrival, created_at
                FROM airline_ratings
                ORDER BY airline_iata_code, created_at DESC
            )
            SELECT 
                lr.airline_iata_code,
                a.name AS airline_name,
                lr.rating_departure,
                lr.rating_arrival,
                lr.created_at
            FROM latest_ratings lr
            JOIN airlines a ON lr.airline_iata_code = a.iata_code
            ORDER BY lr.rating_departure DESC, lr.rating_arrival DESC, lr.created_at DESC
            LIMIT ?;
            """;
            
        return jdbcTemplate.query(sql, ps -> {
            ps.setInt(1, limit);
        }, (rs, rowNum) -> new AirlineRating(
            rs.getString("airline_iata_code"),
            rs.getString("airline_name"),
            rs.getDouble("rating_departure"),
            rs.getDouble("rating_arrival"),
            rs.getTimestamp("created_at").toLocalDateTime()
        ));
    }
}