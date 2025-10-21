package com.rating.punctuality.rating_punctuality.repository.external;

import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AirportStatRepository {
    private final JdbcTemplate jdbcTemplate;
    
    public AirportStatRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<String, Object> getStats(String iataCode) {
        String sql = """
        SELECT 
            (SELECT COUNT(*) FROM flights WHERE departure_airport = ?) AS departures,
            (SELECT COUNT(*) FROM flights WHERE arrival_airport = ?) AS arrivals,
            (SELECT COUNT(*) FROM flights WHERE departure_airport = ? AND fact_departure IS NULL) AS missing_departures,
            (SELECT COUNT(*) FROM flights WHERE arrival_airport = ? AND fact_arrival IS NULL) AS missing_arrivals,
            (SELECT COUNT(*) FROM flight_features WHERE departure_airport = ? OR arrival_airport = ?) AS features_recorded;
                    """;
        
        
        
        return jdbcTemplate.queryForMap(sql, iataCode, iataCode, iataCode, iataCode, iataCode, iataCode);
    }
}
