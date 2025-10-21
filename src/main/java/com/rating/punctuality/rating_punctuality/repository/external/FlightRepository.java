package com.rating.punctuality.rating_punctuality.repository.external;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import com.rating.punctuality.rating_punctuality.model.external.Flight;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class FlightRepository {
    private final JdbcTemplate jdbcTemplate;
    
    public FlightRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Flight> searchFlights(
            String airline,
            String departureAirport,
            String arrivalAirport,
            LocalDate dateFrom,
            LocalDate dateTo,
            Integer minDelay,
            Integer maxDelay,
            int limit) {
        
        List<String> conditions = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        
        String baseSql = """
                SELECT 
                    f.id, f.iata_code, f.flight,
                    f.departure_airport, f.arrival_airport,
                    f.plan_departure, f.plan_arrival,
                    f.fact_departure, f.fact_arrival,
                    ff.day_of_week, ff.time_of_day, 
                    ff.season, ff.delay_category
                FROM flights f
                LEFT JOIN flight_features ff ON f.id = ff.flight_id
                WHERE 1=1
                """;
        
        if (airline != null && !airline.isEmpty()) {
            conditions.add("f.iata_code = ?");
            params.add(airline);
        }
        
        if (departureAirport != null && !departureAirport.isEmpty()) {
            conditions.add("f.departure_airport = ?");
            params.add(departureAirport);
        }
        
        if (arrivalAirport != null && !arrivalAirport.isEmpty()) {
            conditions.add("f.arrival_airport = ?");
            params.add(arrivalAirport);
        }
        
        if (dateFrom != null) {
            conditions.add("DATE(f.plan_departure) >= ?");
            params.add(dateFrom);
        }
        
        if (dateTo != null) {
            conditions.add("DATE(f.plan_departure) <= ?");
            params.add(dateTo);
        }
        
        if (minDelay != null || maxDelay != null) {
            conditions.add("""
                EXTRACT(EPOCH FROM (f.fact_arrival - f.plan_arrival)) 
                BETWEEN COALESCE(?, -1000000) AND COALESCE(?, 1000000)
                """);
            params.add(minDelay);
            params.add(maxDelay);
        }
        
        if (!conditions.isEmpty()) {
            baseSql += " AND " + String.join(" AND ", conditions);
        }
        
        baseSql += " ORDER BY f.plan_departure DESC LIMIT ?";
        params.add(limit);
        
        return jdbcTemplate.query(baseSql, (rs, rowNum) -> 
            new Flight(
                rs.getLong("id"),
                rs.getString("iata_code"),
                rs.getString("flight"),
                rs.getString("departure_airport"),
                rs.getString("arrival_airport"),
                rs.getTimestamp("plan_departure").toLocalDateTime(),
                rs.getTimestamp("plan_arrival").toLocalDateTime(),
                rs.getTimestamp("fact_departure").toLocalDateTime(),
                rs.getTimestamp("fact_arrival").toLocalDateTime(),
                rs.getString("day_of_week"),
                rs.getString("time_of_day"),
                rs.getString("season"),
                rs.getString("delay_category")
            ),
            params.toArray()
        );
    }
}