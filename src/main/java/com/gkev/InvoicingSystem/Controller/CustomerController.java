package com.gkev.InvoicingSystem.Controller;

import com.gkev.InvoicingSystem.Service.UserService;
import com.gkev.InvoicingSystem.models.DTO.UserRegDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final UserService userService;

    @GetMapping("/register")
    @PreAuthorize("hasRole('MANAGER')")
    public Mono<ResponseEntity<String>> registerNewCustomer(UserRegDTO userRegDTO) {
        return userService.registerUser(userRegDTO)
                .map(user -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(user)
                );

    }
}
