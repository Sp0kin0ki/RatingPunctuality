package edu.rutmiit.enterprise.library.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.jwt.MappedJwtClaimSetConverter;

import java.util.*;
import java.util.stream.Collectors;

public class JwtRoleConverter implements Converter<Map<String, Object>, Map<String, Object>> {

    private final MappedJwtClaimSetConverter delegate = MappedJwtClaimSetConverter.withDefaults(Collections.emptyMap());

    @Override
    public Map<String, Object> convert(Map<String, Object> claims) {
        Map<String, Object> convertedClaims = this.delegate.convert(claims);

        Object realmAccess = convertedClaims.get("realm_access");
        if (realmAccess instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> realmMap = (Map<String, Object>) realmAccess;
            Object roles = realmMap.get("roles");
            
            if (roles instanceof Collection) {
                @SuppressWarnings("unchecked")
                Collection<String> rolesList = (Collection<String>) roles;
                List<String> authorities = rolesList.stream()
                        .map(role -> "ROLE_" + role.toUpperCase())
                        .collect(Collectors.toList());
                convertedClaims.put("scope", authorities);
            }
        }

        return convertedClaims;
    }
}
