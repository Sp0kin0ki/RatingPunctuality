package com.rating.punctuality.rating_punctuality;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class RatingPunctualityApplication {

	public static void main(String[] args) {
		SpringApplication.run(RatingPunctualityApplication.class, args);
	}

}
