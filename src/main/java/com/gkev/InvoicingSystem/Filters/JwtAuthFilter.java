package com.gkev.InvoicingSystem.Filters;

import com.gkev.InvoicingSystem.Service.JwtService;
import com.gkev.InvoicingSystem.Service.MyUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.HttpCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Optional;

@RequiredArgsConstructor
public class JwtAuthFilter implements WebFilter {
    private final JwtService jwtService;
    private final MyUserDetailsService myUserDetailsService;

    @Override
    @NullMarked
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        HttpCookie authCookie = exchange.getRequest().getCookies().getFirst("auth_token");
        if (authCookie == null) {
            return chain.filter(exchange);
        }
        String token = authCookie.getValue();
        if (token.isBlank()) {
            return chain.filter(exchange);
        }
        String email;
        try {
            email = jwtService.extractEmail(token);
        } catch (Exception e) {
//            I will add new good error handling later
            return chain.filter(exchange);
        }

        Mono<SecurityContext> contextMono = myUserDetailsService.findByUsername(email)
                .filter(user -> jwtService.validateToken(token, user))
                .map(user -> {
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    user,
                                    null,
                                    user.getAuthorities());
                    return (SecurityContext) new SecurityContextImpl(auth);
                });

        return contextMono
                .map(Optional::of)
                .defaultIfEmpty(Optional.empty())
                .flatMap(optionalContext -> {
                    if (optionalContext.isPresent()) {
                        return chain.filter(exchange)
                                .contextWrite(ReactiveSecurityContextHolder
                                        .withSecurityContext(Mono.just(optionalContext.get())));
                    }
                    return chain.filter(exchange);
                });
    }
}