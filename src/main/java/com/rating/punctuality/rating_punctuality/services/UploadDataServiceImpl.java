package com.rating.punctuality.rating_punctuality.services;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.rating.punctuality.rating_punctuality.model.entities.Flight;
import com.rating.punctuality.rating_punctuality.repository.FlightsRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UploadDataServiceImpl implements UploadDataService {
    private final FlightsRepository flightsRepository;

    public UploadDataServiceImpl(FlightsRepository flightsRepository) {
        this.flightsRepository = flightsRepository;
    }

    @Override
    public boolean saveFlights(List<Flight> flights) {
        // for (int i = 0; i < flights.size(); i++) {
        //     log.info(flights.get(i).getIataCode(), flights.get(i));
        // }
        // return true;
        if(flightsRepository.saveAll(flights).size() != 0) {
            return true;
        }
        return false;

    }

    @Override
    public List<Flight> parseCsvFileSimple(MultipartFile file) throws IOException {
        List<Flight> flights = new ArrayList<>();
        String content = new String(file.getBytes());
        String[] lines = content.split("\n");
        
        for (int i = 1; i < lines.length; i++) {
            String[] values = lines[i].split(",");
            
            if (values.length >= 8) {
                Flight flight = new Flight();

                flight.setIataCode(values[0].trim());
                flight.setFlight(values[1].trim());
                flight.setDepartureAirport(values[2].trim());
                flight.setArrivalAirport(values[3].trim());
                flight.setPlanDeparture(parseDateTime(values[4].trim()));
                flight.setPlanArrival(parseDateTime(values[5].trim()));
                flight.setFactDeparture(parseDateTime(values[6].trim()));
                flight.setFactArrival(parseDateTime(values[7].trim()));
                
                flights.add(flight);
            }
        }
        
        return flights;
    }

    private LocalDateTime parseDateTime(String dateStr) {
        if (dateStr == null || dateStr.isEmpty() || dateStr.equalsIgnoreCase("null")) {
            return null;
        }
        
        try {
            return LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException e) {
            try {
                return LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            } catch (DateTimeParseException e2) {
                log.warn("Не удалось распарсить дату: {}", dateStr);
                return null;
            }
        }
    }

}
