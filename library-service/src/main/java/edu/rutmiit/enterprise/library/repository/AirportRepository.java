package edu.rutmiit.enterprise.library.repository;

import edu.rutmiit.enterprise.library.entity.Airport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AirportRepository extends JpaRepository<Airport, Long> {
}
