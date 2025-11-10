package com.rating.punctuality.filters;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.rating.punctuality.rating_punctuality.model.external.Company;
import com.rating.punctuality.rating_punctuality.repository.external.CompanyRepository;
import com.rating.punctuality.rating_punctuality.service.TokenService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private static final String API_KEY_HEADER = "X-API-KEY";

    private CompanyRepository companyRepository;
    private final CacheManager cacheManager;
    private final TokenService tokenService;

    private final Cache<String, Long> tokenCache;

    public ApiKeyAuthFilter(CompanyRepository companyRepository, TokenService tokenService, CacheManager cacheManager) {
        this.companyRepository = companyRepository;
        this.tokenService = tokenService;
        this.cacheManager = cacheManager;
        this.tokenCache = Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.HOURS).maximumSize(1000).build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String apiKey = request.getHeader(API_KEY_HEADER);

        if (apiKey != null) {
            Company company = companyRepository.findByApiToken(apiKey)
                    .orElse(null);

            if (company != null && company.isActive()) {
                // Создаем аутентификацию для компании
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        company.getNameCompany(),
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_COMPANY")));
                SecurityContextHolder.getContext().setAuthentication(auth);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid API token");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}