package edu.rutmiit.enterprise.library.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "flights")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String flightNumber; // Номер рейса (например, SU123)

    @ManyToOne
    @JoinColumn(name = "departure_airport_id", nullable = false)
    private Airport departureAirport;

    @ManyToOne
    @JoinColumn(name = "arrival_airport_id", nullable = false)
    private Airport arrivalAirport;

    @Column(nullable = false)
    private LocalDateTime scheduledDeparture; // Плановое время вылета

    @Column
    private LocalDateTime actualDeparture; // Фактическое время вылета

    @Column(nullable = false)
    private LocalDateTime scheduledArrival; // Плановое время прилета

    @Column
    private LocalDateTime actualArrival; // Фактическое время прилета

    @Column
    private Integer delayMinutes; // Задержка в минутах

    @Column(length = 50)
    private String status; // Статус: SCHEDULED, DEPARTED, ARRIVED, DELAYED, CANCELLED
}
