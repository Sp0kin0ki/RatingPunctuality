package com.rating.punctuality.rating_punctuality.repository.internal;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.rating.punctuality.rating_punctuality.model.internal.CalculateAirlinePunctuality;
import com.rating.punctuality.rating_punctuality.model.internal.CalculateFlightDirection;

@Repository
public class CalculateRepository {
    private final JdbcTemplate jdbcTemplate;
    
    public CalculateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<CalculateFlightDirection> getFlightDirection() {
        String sql = """
            WITH DirectionStats AS (
                SELECT 
                    LEAST(f.departure_airport, f.arrival_airport) AS airport1,
                    GREATEST(f.departure_airport, f.arrival_airport) AS airport2,
                    COUNT(*) AS total_flights,
                    SUM(
                        CASE 
                            WHEN f.fact_arrival IS NOT NULL 
                                AND ABS(EXTRACT(EPOCH FROM (f.fact_arrival - f.plan_arrival))) < 900 
                            THEN 1 
                            ELSE 0 
                        END
                    ) AS on_time_arrivals,
                    ROUND(AVG(
                        CASE 
                            WHEN f.fact_arrival IS NOT NULL 
                            THEN EXTRACT(EPOCH FROM (f.fact_arrival - f.plan_arrival))/60 
                            ELSE NULL
                        END
                    )::numeric, 1) AS avg_delay_minutes,
                    SUM(
                        CASE 
                            WHEN f.fact_departure IS NULL 
                            THEN 1 
                            ELSE 0 
                        END
                    ) AS missing_departure_count
                FROM flights f
                GROUP BY 
                    LEAST(f.departure_airport, f.arrival_airport), 
                    GREATEST(f.departure_airport, f.arrival_airport)
            )
            SELECT 
                airport1,
                airport2,
                total_flights,
                on_time_arrivals,
                ROUND(
                    (on_time_arrivals * 100.0 / NULLIF(total_flights, 0))::numeric, 
                    1
                ) AS on_time_percentage,
                COALESCE(avg_delay_minutes, 0) AS avg_delay_minutes,
                missing_departure_count
            FROM DirectionStats
            ORDER BY airport1, airport2;
                        """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> new CalculateFlightDirection(
            rs.getString("airport1"),
            rs.getString("airport2"),
            rs.getInt("total_flights"),
            rs.getInt("on_time_arrivals"),
            rs.getDouble("on_time_percentage"),
            rs.getDouble("avg_delay_minutes"),
            rs.getInt("missing_departure_count")
        ));
    }

    public List<CalculateAirlinePunctuality> getAirlinePunctuality() {
        String sql = """
            WITH FlightStats AS (
                    SELECT
                        f.iata_code AS code,
                        a.name AS airline,
                        COUNT(*) AS total_flights,
                        COUNT(CASE 
                                WHEN f.fact_departure IS NOT NULL 
                                AND EXTRACT(EPOCH FROM (f.fact_departure - f.plan_departure)) < 900 
                                THEN 1 
                            END) AS on_time_departures,
                        COUNT(CASE 
                                WHEN f.fact_arrival IS NOT NULL 
                                AND EXTRACT(EPOCH FROM (f.fact_arrival - f.plan_arrival)) < 900 
                                THEN 1 
                            END) AS on_time_arrivals,
                        COUNT(CASE 
                                WHEN f.fact_departure IS NULL 
                                THEN 1 
                            END) AS cancellations
                    FROM flights f
                    LEFT JOIN airlines a ON f.iata_code = a.iata_code
                    GROUP BY f.iata_code, a.name
                )
                SELECT
                    code,
                    airline,
                    total_flights,
                    ROUND(
                        (on_time_departures * 100.0 / NULLIF(total_flights, 0))::numeric, 
                        1
                    )::FLOAT AS departure_percentage,
                    ROUND(
                        (on_time_arrivals * 100.0 / NULLIF(total_flights, 0))::numeric, 
                        1
                    )::FLOAT AS arrival_percentage,
                    ROUND(
                        (cancellations * 100.0 / NULLIF(total_flights, 0))::numeric, 
                        1
                    )::FLOAT AS cancellation_percentage
                FROM FlightStats
                ORDER BY (on_time_departures * 100.0 / NULLIF(total_flights, 0)) DESC, 
                         (on_time_arrivals * 100.0 / NULLIF(total_flights, 0)) DESC;
                        """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> new CalculateAirlinePunctuality(
            rs.getString("code"),
            rs.getString("airline"),
            rs.getInt("total_flights"),
            rs.getDouble("departure_percentage"),
            rs.getDouble("arrival_percentage"),
            rs.getDouble("cancellation_percentage")
        ));
    }
}
