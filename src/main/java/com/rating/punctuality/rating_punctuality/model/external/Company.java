package com.rating.punctuality.rating_punctuality.model.external;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

@Entity
@Table(name = "companies")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Company {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name_company", unique = true, nullable = false)
    private String nameCompany;
    
    @Column(name = "api_token", unique = true, nullable = false)
    private String apiToken;
    
    @Column(name = "is_active")
    private boolean isActive = true;
}