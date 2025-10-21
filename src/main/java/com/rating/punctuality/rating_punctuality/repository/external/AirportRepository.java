package com.rating.punctuality.rating_punctuality.repository.external;

import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.rating.punctuality.rating_punctuality.model.external.Airport;

@Repository
public class AirportRepository {
    private final JdbcTemplate jdbcTemplate;
    
    public AirportRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Airport> getAirports(String city) {
        List<String> conditions = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        
        String baseSql = """
                SELECT 
                    iata_code, airport_name, city, timezone,
                    longitude, latitude
                FROM airports
                WHERE 1=1
                """;
        
        if (city != null && !city.equals("default")) {
            conditions.add("LOWER(city) LIKE LOWER(?)");
            params.add("%" + city + "%");
        }
        
        if (!conditions.isEmpty()) {
            baseSql += " AND " + String.join(" AND ", conditions);
        }
        
        return jdbcTemplate.query(baseSql, (rs, rowNum) -> 
            new Airport(
                rs.getString("iata_code"),
                rs.getString("airport_name"),
                rs.getString("city"),
                rs.getString("timezone"),
                rs.getDouble("longitude"),
                rs.getDouble("latitude")
            ),
            params.toArray()
        );
    }
}
