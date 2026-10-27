package com.rating.punctuality.recommendation.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost:65535/unused"
})
@AutoConfigureMockMvc
class RecommendationControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void recommendationsRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/internal/recommendations/22222222-2222-4222-8222-222222222222"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void serviceCanReadRecommendations() throws Exception {
        mockMvc.perform(get("/internal/recommendations/22222222-2222-4222-8222-222222222222")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_SERVICE"))))
                .andExpect(status().isOk());
    }

    @Test
    void operatorCannotReadRecommendations() throws Exception {
        mockMvc.perform(get("/internal/recommendations/22222222-2222-4222-8222-222222222222")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_OPERATOR"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void operatorCanReadAdminInfo() throws Exception {
        mockMvc.perform(get("/internal/admin/info")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_OPERATOR"))))
                .andExpect(status().isOk());
    }

    @Test
    void serviceCannotReadAdminInfo() throws Exception {
        mockMvc.perform(get("/internal/admin/info")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_SERVICE"))))
                .andExpect(status().isForbidden());
    }
}
