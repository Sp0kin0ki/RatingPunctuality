package com.rating.punctuality.rating_punctuality.repository;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.rating.punctuality.rating_punctuality.model.Airport;

@Repository
public class AirportRepository {
    private final JdbcTemplate jdbcTemplate;
    
    public AirportRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Airport> getAllAirports() {
        String sql = """
            SELECT 
                a.iata_code as iata_code,
                a.airport_name as airport_name,
                a.longitude as longitude,
                a.latitude as latitude,
                COALESCE(dep.departure_count, 0) as departure_count,
                COALESCE(arr.arrival_count, 0) as arrival_count
            FROM airports a
            LEFT JOIN (
                SELECT departure_airport, COUNT(*) as departure_count
                FROM flights GROUP BY departure_airport
            ) dep ON a.iata_code = dep.departure_airport
            LEFT JOIN (
                SELECT arrival_airport, COUNT(*) as arrival_count
                FROM flights GROUP BY arrival_airport
            ) arr ON a.iata_code = arr.arrival_airport
            """;
        
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Airport airport = new Airport();
            airport.setIataCode(rs.getString("iata_code"));
            airport.setAirportName(rs.getString("airport_name")); 
            airport.setLongitude(rs.getDouble("longitude"));
            airport.setLatitude(rs.getDouble("latitude"));
            airport.setCountDeparture(rs.getInt("departure_count"));
            airport.setCountArrival(rs.getInt("arrival_count"));
            return airport;
        });
    }
}
