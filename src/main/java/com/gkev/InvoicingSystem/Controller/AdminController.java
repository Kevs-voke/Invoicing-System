package com.gkev.InvoicingSystem.Controller;

import com.gkev.InvoicingSystem.Service.UserService;
import com.gkev.InvoicingSystem.models.DTO.AdminCreateUserDTO;
import com.gkev.InvoicingSystem.models.DTO.AdminCreateUserResDTO;
import com.gkev.InvoicingSystem.models.DTO.AdminUserListItemDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('MANAGER') or (hasRole('STAFF') and #adminCreateUserDTO.role() == 'CUSTOMER')")
    public Mono<ResponseEntity<AdminCreateUserResDTO>> createUser(@Valid @RequestBody AdminCreateUserDTO adminCreateUserDTO) {
        return userService.createUserByAdmin(adminCreateUserDTO)
                .map(created -> ResponseEntity.status(HttpStatus.CREATED).body(created));
    }

    @GetMapping
    @PreAuthorize("hasRole('MANAGER')")
    public Mono<ResponseEntity<List<AdminUserListItemDTO>>> listUsers() {
        return userService.listUsers()
                .map(ResponseEntity::ok);
    }
}
