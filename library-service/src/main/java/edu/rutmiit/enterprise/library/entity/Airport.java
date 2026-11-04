package edu.rutmiit.enterprise.library.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "airports")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Airport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 3)
    private String code; // IATA код (например, SVO, DME, LED)

    @Column(nullable = false)
    private String name; // Название аэропорта

    @Column(nullable = false)
    private String city; // Город
}
