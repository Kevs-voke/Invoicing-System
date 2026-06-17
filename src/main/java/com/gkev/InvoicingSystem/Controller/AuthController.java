package com.gkev.InvoicingSystem.Controller;


import com.gkev.InvoicingSystem.Service.UserService;
import com.gkev.InvoicingSystem.models.DTO.CusRegDTO;
import com.gkev.InvoicingSystem.models.DTO.LoginReqDTO;
import com.gkev.InvoicingSystem.models.DTO.LoginResApiDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private  final UserService userService;

    @PostMapping("/register")
    public Mono<ResponseEntity<LoginResApiDTO>> login(@Valid @RequestBody CusRegDTO cusRegDTO) {
        return userService.registerCust(cusRegDTO)
                .map(authResponse -> ResponseEntity.ok()
                        .headers(buildCookieHeaders(authResponse.jwtToken()))
                        .body(new LoginResApiDTO(
                                authResponse.email(),
                                authResponse.roles()
                        ))
                );
    }
    @PostMapping("/login")
    public Mono<ResponseEntity<LoginResApiDTO>> login(@Valid @RequestBody LoginReqDTO loginReqDTO) {
        return userService.loginCust(loginReqDTO)
                .map( loginResponse ->
                        ResponseEntity.ok()
                                .headers(buildCookieHeaders(loginResponse.jwtToken()))
                                .body(new LoginResApiDTO(loginResponse.email(),
                                        loginResponse.roles()))
                );
    }

    private HttpHeaders buildCookieHeaders(String token) {
        ResponseCookie cookie = ResponseCookie.from("auth_token", token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofHours(24))
                .sameSite("None")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
        return headers;
    }
}
