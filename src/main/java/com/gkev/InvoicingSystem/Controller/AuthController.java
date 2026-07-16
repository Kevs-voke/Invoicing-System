package com.gkev.InvoicingSystem.Controller;


import com.gkev.InvoicingSystem.Service.UserService;
import com.gkev.InvoicingSystem.models.DTO.CusRegDTO;
import com.gkev.InvoicingSystem.models.DTO.LoginReqDTO;
import com.gkev.InvoicingSystem.models.DTO.LoginResApiDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static com.gkev.InvoicingSystem.Utils.CookieHeaderBuilderUtils.buildCookieHeaders;
import static com.gkev.InvoicingSystem.Utils.CookieHeaderBuilderUtils.clearCookieHeaders;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/register")
    public Mono<ResponseEntity<LoginResApiDTO>> userSelfReg(@Valid @RequestBody CusRegDTO cusRegDTO) {
        return userService.CustSelfReg(cusRegDTO)
                .map(authResponse -> ResponseEntity.ok()
                        .headers(buildCookieHeaders(authResponse.jwtToken()))
                        .body(new LoginResApiDTO(
                                authResponse.firstName(),
                                authResponse.roles()
                        ))
                );
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<LoginResApiDTO>> login(@Valid @RequestBody LoginReqDTO loginReqDTO) {
        return userService.loginUser(loginReqDTO)
                .map(loginResponse ->
                        ResponseEntity.ok()
                                .headers(buildCookieHeaders(loginResponse.jwtToken()))
                                .body(new LoginResApiDTO(loginResponse.firstName(),
                                        loginResponse.roles()))
                );
    }

    @PostMapping("/logout")
    public Mono<ResponseEntity<Void>> logout() {
        return Mono.just(
                ResponseEntity.ok()
                        .headers(clearCookieHeaders())
                        .build()
        );
    }
}

