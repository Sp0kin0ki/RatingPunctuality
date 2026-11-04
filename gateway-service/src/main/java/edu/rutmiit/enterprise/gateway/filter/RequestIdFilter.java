package edu.rutmiit.enterprise.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Component
public class RequestIdFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(RequestIdFilter.class);
    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    private static final String MDC_REQUEST_ID_KEY = "request_id";
    private static final Pattern UUID_PATTERN = Pattern.compile(
        "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        List<String> headerValues = request.getHeaders().get(REQUEST_ID_HEADER);
        String requestId = extractAndValidateRequestId(headerValues);
        
        MDC.put(MDC_REQUEST_ID_KEY, requestId);

        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .header(REQUEST_ID_HEADER, requestId)
                .build();

        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(mutatedRequest)
                .build();

        mutatedExchange.getResponse().getHeaders().set(REQUEST_ID_HEADER, requestId);

        return chain.filter(mutatedExchange)
                .contextWrite(ctx -> ctx.put(MDC_REQUEST_ID_KEY, requestId))
                .doFinally(signalType -> MDC.remove(MDC_REQUEST_ID_KEY));
    }

    private String extractAndValidateRequestId(List<String> headerValues) {
        if (headerValues == null || headerValues.isEmpty()) {
            return generateRequestId();
        }

        if (headerValues.size() > 1) {
            log.warn("Multiple X-Request-ID headers received, using first one");
        }

        String headerValue = headerValues.get(0);
        if (headerValue == null || headerValue.isBlank()) {
            return generateRequestId();
        }

        String normalized = headerValue.trim().toLowerCase();

        if (UUID_PATTERN.matcher(normalized).matches()) {
            return normalized;
        } else {
            log.warn("Invalid X-Request-ID format received: {}, generating new one", headerValue);
            return generateRequestId();
        }
    }

    private String generateRequestId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
