package com.gkev.InvoicingSystem.Filters;

import com.gkev.InvoicingSystem.models.UserPrincipal;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;


//Blocks any authenticated request from a user whose password must be changed
 
public class PasswordChangeRequiredFilter implements WebFilter {

    private static final List<String> ALLOWED_PREFIXES = List.of(
            "/auth/",
            "/me",
            "/reports/",
            "/css/"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (request.getMethod() == HttpMethod.OPTIONS || isAllowed(request.getPath().value())) {
            return chain.filter(exchange);
        }

        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal)
                .filter(UserPrincipal.class::isInstance)
                .cast(UserPrincipal.class)
                .map(UserPrincipal::getMustChangePassword)
                .defaultIfEmpty(false)
                .flatMap(mustChangePassword -> {
                    if (Boolean.TRUE.equals(mustChangePassword)) {
                        return rejectWithPasswordChangeRequired(exchange);
                    }
                    return chain.filter(exchange);
                });
    }

    private boolean isAllowed(String path) {
        return ALLOWED_PREFIXES.stream().anyMatch(path::startsWith);
    }

    private Mono<Void> rejectWithPasswordChangeRequired(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        byte[] body = ("{\"errorCode\":\"PASSWORD_CHANGE_REQUIRED\"," +
                "\"message\":\"You must change your temporary password before continuing.\"}")
                .getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(body);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}