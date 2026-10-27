package com.rating.punctuality.recommendation.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class JwtRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private static final Set<String> SUPPORTED_ROLES = Set.of("service", "operator");

    private final JwtGrantedAuthoritiesConverter scopes = new JwtGrantedAuthoritiesConverter();

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Set<GrantedAuthority> result = new LinkedHashSet<>(scopes.convert(jwt));

        Object claim = jwt.getClaim("realm_access");
        if (claim instanceof Map<?, ?> realm && realm.get("roles") instanceof Collection<?> roles) {
            for (Object value : roles) {
                if (value instanceof String role && SUPPORTED_ROLES.contains(role)) {
                    result.add(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase(Locale.ROOT)));
                }
            }
        }

        return result;
    }
}
