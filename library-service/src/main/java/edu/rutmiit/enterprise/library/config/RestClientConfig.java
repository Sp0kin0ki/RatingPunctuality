package edu.rutmiit.enterprise.library.config;

import io.micrometer.observation.ObservationRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.observation.ClientRequestObservationConvention;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    private static final Logger log = LoggerFactory.getLogger(RestClientConfig.class);

    @Value("${recommendation.service.url}")
    private String recommendationServiceUrl;

    @Bean
    public RestClient recommendationRestClient(
            OAuth2AuthorizedClientManager authorizedClientManager,
            ObservationRegistry observationRegistry) {
        
        return RestClient.builder()
                .baseUrl(recommendationServiceUrl)
                .requestInterceptor((request, body, execution) -> {
                    // Проброс X-Request-ID из MDC
                    String requestId = MDC.get("request_id");
                    if (requestId != null) {
                        request.getHeaders().set("X-Request-ID", requestId);
                    }
                    
                    log.debug("Calling recommendation-service with X-Request-ID: {}", requestId);
                    return execution.execute(request, body);
                })
                .requestInterceptor(new OAuth2ClientHttpRequestInterceptor(authorizedClientManager))
                .observationRegistry(observationRegistry)
                .build();
    }

    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientService authorizedClientService) {

        OAuth2AuthorizedClientProvider authorizedClientProvider =
                OAuth2AuthorizedClientProviderBuilder.builder()
                        .clientCredentials()
                        .build();

        AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager =
                new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                        clientRegistrationRepository, authorizedClientService);
        
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

        return authorizedClientManager;
    }
}
