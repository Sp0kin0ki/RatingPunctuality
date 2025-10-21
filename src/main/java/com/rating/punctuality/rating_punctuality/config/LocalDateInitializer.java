package com.rating.punctuality.rating_punctuality.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.rating.punctuality.rating_punctuality.model.internal.CalculateAirlinePunctuality;
import com.rating.punctuality.rating_punctuality.model.internal.CalculateFlightDirection;
import com.rating.punctuality.rating_punctuality.repository.internal.CalculateRepository;

@Component
@Slf4j
@RequiredArgsConstructor
public class LocalDateInitializer implements CommandLineRunner {

    private final CalculateRepository calculateRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("=== ВЫПОЛНЕНИЕ ПРЕДВАРИТЕЛЬНЫХ ОПЕРАЦИЙ С БАЗОЙ ===");
        
        performInitialQueries();
        
        log.info("=== ПРЕДВАРИТЕЛЬНЫЕ ОПЕРАЦИИ ЗАВЕРШЕНЫ ===");
    }
    
    private void performInitialQueries() {
        log.info("Начало экспорта данных...");
        
        List<CalculateFlightDirection> directions = calculateRepository.getFlightDirection();
        List<CalculateAirlinePunctuality> punctuality = calculateRepository.getAirlinePunctuality();

        exportToJson(directions, punctuality);
        
        log.info("Экспорт завершен. Записей в direction: {}. Записей в punctuality: {}", directions.size(), punctuality.size());
    }

    private void exportToJson(List<CalculateFlightDirection> directions, List<CalculateAirlinePunctuality> punctuality) {
    try {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        
        
        mapper.writeValue(new File("/home/vladimir/Загрузки/rating_punctuality/src/main/resources/templates/flight_direction_stats.json"), directions);
        mapper.writeValue(new File("/home/vladimir/Загрузки/rating_punctuality/src/main/resources/templates/airline_punctuality.json"), punctuality);
        
        log.info("Файлы сохранены: directions.json и airline_punctuality.json");
        
    } catch (Exception e) {
        log.error("Ошибка при сохранении JSON файлов", e);
    }
}
}