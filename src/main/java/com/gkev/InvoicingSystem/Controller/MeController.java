package com.gkev.InvoicingSystem.Controller;

import com.gkev.InvoicingSystem.Service.UserService;
import com.gkev.InvoicingSystem.models.DTO.MeDTO;
import com.gkev.InvoicingSystem.models.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/me")
@RequiredArgsConstructor
public class MeController {
    private final UserService userService;

    @GetMapping
    public Mono<ResponseEntity<MeDTO>> getMe(Authentication authentication) {
       UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        UUID id = null;
        if (userPrincipal != null) {
             id = userPrincipal.getUserId();
        }
        return userService.getMe(id)
                .map(body -> ResponseEntity.ok().body(body));



    }
}
