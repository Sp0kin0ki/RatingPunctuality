package com.rating.punctuality.rating_punctuality.repository;

import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DepartureDelaysRepository {
    private final JdbcTemplate jdbcTemplate;
    
    public DepartureDelaysRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<String, Object> getDepartureDelays() {
        String sql = """
            SELECT
                COUNT(*) FILTER (WHERE EXTRACT(EPOCH FROM (fact_departure - plan_departure)) <= 600) AS "0-10 минут",
                COUNT(*) FILTER (WHERE EXTRACT(EPOCH FROM (fact_departure - plan_departure)) > 600 AND EXTRACT(EPOCH FROM ((fact_departure - plan_departure))) <= 1200) AS "11-20 минут",
                COUNT(*) FILTER (WHERE EXTRACT(EPOCH FROM (fact_departure - plan_departure)) > 1200 AND EXTRACT(EPOCH FROM (fact_departure - plan_departure)) <= 1800) AS "21-30 минут",
                COUNT(*) FILTER (WHERE EXTRACT(EPOCH FROM (fact_departure - plan_departure)) > 1800 AND EXTRACT(EPOCH FROM (fact_departure - plan_departure)) <= 7200) AS "31-120 минут",
                COUNT(*) FILTER (WHERE EXTRACT(EPOCH FROM (fact_departure - plan_departure)) > 7200) AS ">120 минут"
            FROM flights;
                        """;
        
        Map<String, Object> result = jdbcTemplate.queryForMap(sql);
        
        return Map.of(
            "0_10_min", result.get("0-10 минут"),
            "11_20_min", result.get("11-20 минут"), 
            "21_30_min", result.get("21-30 минут"),
            "31_120_min", result.get("31-120 минут"),
            "over_120_min", result.get(">120 минут")
        );
    }
}
