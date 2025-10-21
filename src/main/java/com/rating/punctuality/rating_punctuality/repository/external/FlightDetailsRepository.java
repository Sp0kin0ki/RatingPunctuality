package com.rating.punctuality.rating_punctuality.repository.external;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.rating.punctuality.rating_punctuality.model.external.FlightDetails;

@Repository
public class FlightDetailsRepository {
    private final JdbcTemplate jdbcTemplate;
    
    public FlightDetailsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<FlightDetails> getDetails(int id) {
        String sql = """
            SELECT 
                f.*, 
                dep.airport_name AS departure_airport_name,
                dep.city AS departure_city,
                arr.airport_name AS arrival_airport_name,
                arr.city AS arrival_city,
                ff.day_of_week, ff.time_of_day, 
                ff.season, ff.delay_category
            FROM flights f
            JOIN airports dep ON f.departure_airport = dep.iata_code
            JOIN airports arr ON f.arrival_airport = arr.iata_code
            LEFT JOIN flight_features ff ON f.id = ff.flight_id
            WHERE f.id = ?
                        """;

        return jdbcTemplate.query(sql, ps -> {
            ps.setInt(1, id);
        }, (rs, rowNum) -> new FlightDetails(
            rs.getInt("id"),
            rs.getString("iata_code"),
            rs.getString("flight"),
            rs.getString("departure_airport"),
            rs.getString("arrival_airport"),
            rs.getTimestamp("plan_departure").toLocalDateTime(),
            rs.getTimestamp("fact_departure").toLocalDateTime(),
            rs.getTimestamp("plan_arrival").toLocalDateTime(),
            rs.getTimestamp("fact_arrival").toLocalDateTime(),
            rs.getString("departure_airport_name"),
            rs.getString("departure_city"),
            rs.getString("arrival_airport_name"),
            rs.getString("arrival_city"),
            rs.getString("day_of_week"),
            rs.getString("time_of_day"),
            rs.getString("season"),
            rs.getString("delay_category")
        ));
    }
}
