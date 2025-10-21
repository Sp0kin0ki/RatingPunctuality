package com.rating.punctuality.rating_punctuality.repository.external;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.rating.punctuality.rating_punctuality.model.external.DelayStats;

@Repository
public class DelayStatsRepository {
    private final JdbcTemplate jdbcTemplate;
    
    public DelayStatsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<DelayStats> getDelayStats(String iataCode) {

        String sql = """
            SELECT 
                ff.delay_category,
                COUNT(*) AS count,
                ROUND(AVG(EXTRACT(EPOCH FROM (f.fact_arrival - f.plan_arrival)))) AS avg_delay_seconds
            FROM flights f
            JOIN flight_features ff ON f.id = ff.flight_id
            WHERE f.iata_code = ?
            GROUP BY ff.delay_category
                        """;

        return jdbcTemplate.query(sql, ps -> {
            ps.setString(1, iataCode);
        }, (rs, rowNum) -> new DelayStats(
            rs.getString("delay_category"),
            rs.getInt("count"),
            rs.getInt("avg_delay_seconds")
        ));
    }
}
