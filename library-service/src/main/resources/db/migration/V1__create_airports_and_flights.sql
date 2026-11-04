CREATE TABLE airports (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(3) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL
);

CREATE TABLE flights (
    id BIGSERIAL PRIMARY KEY,
    flight_number VARCHAR(255) NOT NULL UNIQUE,
    departure_airport_id BIGINT NOT NULL,
    arrival_airport_id BIGINT NOT NULL,
    scheduled_departure TIMESTAMP NOT NULL,
    actual_departure TIMESTAMP,
    scheduled_arrival TIMESTAMP NOT NULL,
    actual_arrival TIMESTAMP,
    delay_minutes INTEGER,
    status VARCHAR(50),
    CONSTRAINT fk_departure_airport FOREIGN KEY (departure_airport_id) REFERENCES airports(id),
    CONSTRAINT fk_arrival_airport FOREIGN KEY (arrival_airport_id) REFERENCES airports(id)
);

-- Insert sample airports
INSERT INTO airports (code, name, city) VALUES 
    ('SVO', 'Sheremetyevo International Airport', 'Moscow'),
    ('DME', 'Domodedovo International Airport', 'Moscow'),
    ('LED', 'Pulkovo Airport', 'Saint Petersburg');

-- Insert sample flights
INSERT INTO flights (flight_number, departure_airport_id, arrival_airport_id, 
                     scheduled_departure, scheduled_arrival, status, delay_minutes) 
VALUES 
    ('SU123', 1, 3, '2026-11-04 10:00:00', '2026-11-04 11:30:00', 'SCHEDULED', 0),
    ('SU456', 3, 1, '2026-11-04 14:00:00', '2026-11-04 15:30:00', 'SCHEDULED', 0),
    ('SU789', 2, 3, '2026-11-04 18:00:00', '2026-11-04 19:30:00', 'SCHEDULED', 0);
