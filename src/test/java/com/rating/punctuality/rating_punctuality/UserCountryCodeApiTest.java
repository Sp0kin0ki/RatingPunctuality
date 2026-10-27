package com.rating.punctuality.rating_punctuality;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class UserCountryCodeApiTest {

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("punctuality_rating_api")
            .withUsername("postgres")
            .withPassword("postgres");

    @Autowired
    private MockMvc mockMvc;

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAcceptLegacyRegistrationBodyWithoutCountryCode() throws Exception {
        mockMvc.perform(post("/api/auth/add-user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "legacy_api_user",
                                  "email": "legacy-api@example.com",
                                  "password": "Password1",
                                  "confirm_password": "Password1",
                                  "description": "Legacy API payload"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.countryCode").value("ZZ"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAcceptExplicitCountryCode() throws Exception {
        mockMvc.perform(post("/api/auth/add-user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "ru_api_user",
                                  "email": "ru-api@example.com",
                                  "password": "Password1",
                                  "confirm_password": "Password1",
                                  "description": "New API payload",
                                  "countryCode": "RU"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.countryCode").value("RU"));
    }
}
