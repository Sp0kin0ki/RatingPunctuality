package com.rating.punctuality.recommendation.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class JwtRoleConverterTest {

    private final JwtRoleConverter converter = new JwtRoleConverter();

    @Test
    void mapsSupportedRealmRolesAndScopes() {
        Jwt jwt = jwt(Map.of(
                "scope", "openid profile",
                "realm_access", Map.of("roles", List.of("service", "operator", "unknown"))
        ));

        Collection<GrantedAuthority> authorities = converter.convert(jwt);

        assertThat(authorities)
                .extracting(GrantedAuthority::getAuthority)
                .contains("SCOPE_openid", "SCOPE_profile", "ROLE_SERVICE", "ROLE_OPERATOR")
                .doesNotContain("ROLE_UNKNOWN");
    }

    @Test
    void ignoresMalformedRealmAccessClaim() {
        Jwt jwt = jwt(Map.of("realm_access", "not-a-map"));

        assertThat(converter.convert(jwt)).isEmpty();
    }

    private Jwt jwt(Map<String, Object> claims) {
        return Jwt.withTokenValue("test-token")
                .header("alg", "RS256")
                .subject("service-account")
                .issuer("http://localhost:8085/realms/course")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(300))
                .claims(existing -> existing.putAll(claims))
                .build();
    }
}
