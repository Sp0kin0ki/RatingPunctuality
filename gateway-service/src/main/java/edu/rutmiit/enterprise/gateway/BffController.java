package edu.rutmiit.enterprise.gateway;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/bff")
public class BffController {

    @GetMapping("/me")
    public Mono<Map<String, Object>> getCurrentUser(@AuthenticationPrincipal OidcUser user) {
        if (user == null) {
            return Mono.just(Map.of("authenticated", false));
        }
        return Mono.just(Map.of(
                "authenticated", true,
                "username", user.getPreferredUsername(),
                "email", user.getEmail(),
                "name", user.getFullName(),
                "roles", user.getAuthorities()
        ));
    }

    @GetMapping("/csrf")
    public Mono<CsrfToken> getCsrfToken(ServerWebExchange exchange) {
        return exchange.getAttribute(CsrfToken.class.getName());
    }
}
