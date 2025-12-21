package com.rating.punctuality.rating_punctuality.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rating.punctuality.rating_punctuality.model.entities.Flight;

public interface FlightsRepository extends JpaRepository<Flight, String> {

}