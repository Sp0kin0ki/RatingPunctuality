package com.rating.punctuality.rating_punctuality.repository;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.rating.punctuality.rating_punctuality.model.CancellationsDistribution;

@Repository
public class CancellationsDistributionRepository {
    private final JdbcTemplate jdbcTemplate;
    
    public CancellationsDistributionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<CancellationsDistribution> getCancellationsDistribution() {
        String sql = """
            SELECT 
                a.name AS airlines,
                COUNT(*) FILTER (WHERE f.fact_departure IS NULL) AS cancellations
            FROM flights f
            JOIN airlines a ON f.iata_code = a.iata_code
            GROUP BY a.name
            ORDER BY cancellations DESC;
                        """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> new CancellationsDistribution(
            rs.getString("airlines"),
            rs.getInt("cancellations")
        ));
    }
}
