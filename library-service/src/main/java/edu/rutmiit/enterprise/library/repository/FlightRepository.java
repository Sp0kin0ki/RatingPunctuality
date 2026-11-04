package edu.rutmiit.enterprise.library.repository;

import edu.rutmiit.enterprise.library.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlightRepository extends JpaRepository<Flight, Long> {
}
