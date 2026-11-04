package edu.rutmiit.enterprise.library.controller;

import edu.rutmiit.enterprise.library.entity.Airport;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/airports")
public class AirportController {

    @GetMapping
    public List<Airport> getAllAirports() {
        // Моковые данные для демонстрации
        return List.of(
            createMockAirport(1L, "SVO", "Sheremetyevo", "Moscow"),
            createMockAirport(2L, "DME", "Domodedovo", "Moscow"),
            createMockAirport(3L, "LED", "Pulkovo", "Saint Petersburg")
        );
    }
    
    private Airport createMockAirport(Long id, String code, String name, String city) {
        Airport airport = new Airport();
        airport.setId(id);
        airport.setCode(code);
        airport.setName(name);
        airport.setCity(city);
        return airport;
    }

    @GetMapping("/{id}")
    public Airport getAirportById(@PathVariable Long id) {
        // Моковые данные
        return createMockAirport(id, "SVO", "Sheremetyevo", "Moscow");
    }

    @PostMapping
    @PreAuthorize("hasRole('EDITOR')")
    @ResponseStatus(HttpStatus.CREATED)
    public Airport createAirport(@RequestBody Airport airport) {
        // Моковое создание для демонстрации
        airport.setId(999L);
        return airport;
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('EDITOR')")
    public Airport updateAirport(@PathVariable Long id, @RequestBody Airport updatedAirport) {
        updatedAirport.setId(id);
        return updatedAirport;
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('EDITOR')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAirport(@PathVariable Long id) {
        // Моковое удаление (ничего не делаем)
    }
}
