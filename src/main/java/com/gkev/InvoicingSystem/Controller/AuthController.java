package com.gkev.InvoicingSystem.Controller;


import com.gkev.InvoicingSystem.Service.UserService;
import com.gkev.InvoicingSystem.models.DTO.ChangePasswordDTO;
import com.gkev.InvoicingSystem.models.DTO.CusRegDTO;
import com.gkev.InvoicingSystem.models.DTO.LoginReqDTO;
import com.gkev.InvoicingSystem.models.DTO.LoginResApiDTO;
import com.gkev.InvoicingSystem.models.UserPrincipal;
import com.gkev.InvoicingSystem.models.DTO.OneTimeLoginDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

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
                                authResponse.roles(),
                                authResponse.mustChangePassword()
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
                                        loginResponse.roles(),
                                        loginResponse.mustChangePassword()))
                );
    }

    @PostMapping("/change-password")
    public Mono<ResponseEntity<Void>> changePassword(@Valid @RequestBody ChangePasswordDTO changePasswordDTO,
                                                       Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        UUID userId = userPrincipal.getUserId();
        return userService.changePassword(userId, changePasswordDTO)
                .then(Mono.just(ResponseEntity.ok().<Void>build()));
    }

    @PostMapping("/one-time-login")
    public Mono<ResponseEntity<LoginResApiDTO>> oneTimeLogin(@Valid @RequestBody OneTimeLoginDTO dto) {
        return userService.loginWithOneTimeToken(dto.token())
                .map(loginResponse -> ResponseEntity.ok()
                        .headers(buildCookieHeaders(loginResponse.jwtToken()))
                        .body(new LoginResApiDTO(loginResponse.firstName(), loginResponse.roles(), loginResponse.mustChangePassword()))
                );
    }

    @PostMapping("/logout")
    public Mono<ResponseEntity<Void>> logout() {
        return Mono.just(
                ResponseEntity.noContent()
                .headers(clearCookieHeaders())
                .build());


    }
}


