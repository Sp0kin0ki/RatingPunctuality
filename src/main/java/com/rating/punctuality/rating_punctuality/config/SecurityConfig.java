package com.rating.punctuality.rating_punctuality.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.rating.punctuality.rating_punctuality.filters.ApiKeyAuthFilter;
import com.rating.punctuality.rating_punctuality.repository.external.CompanyRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CompanyRepository companyRepository;
    @Bean
    public ApiKeyAuthFilter apiKeyAuthFilter() {
        return new ApiKeyAuthFilter(companyRepository);
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/company/**").hasRole("COMPANY")
                .anyRequest().permitAll()
            )
            .addFilterBefore(apiKeyAuthFilter(), BasicAuthenticationFilter.class);

        return http.build();
    }
}
