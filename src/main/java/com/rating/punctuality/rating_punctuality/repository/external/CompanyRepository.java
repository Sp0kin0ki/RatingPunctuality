package com.rating.punctuality.rating_punctuality.repository.external;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rating.punctuality.rating_punctuality.model.external.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByApiToken(String apiToken);
    boolean existsByApiToken(String apiToken);
}