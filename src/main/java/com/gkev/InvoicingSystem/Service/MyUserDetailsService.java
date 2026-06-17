package com.gkev.InvoicingSystem.Service;

import com.gkev.InvoicingSystem.models.DTO.UserWithRolesDTO;
import com.gkev.InvoicingSystem.models.UserPrincipal;
import com.gkev.InvoicingSystem.models.repo.RolesRepo;
import com.gkev.InvoicingSystem.models.repo.UserRolesRepo;
import com.gkev.InvoicingSystem.models.repo.UsersRepo;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class MyUserDetailsService implements ReactiveUserDetailsService {
    private final UsersRepo usersRepo;
    private final UserRolesRepo userRoleRepo;
    private final RolesRepo roleRepo;

    @Override
    @NullMarked
    public Mono<UserDetails> findByUsername(String username) {
        return getUserWithRoles(username)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found")))
                .map(u -> new UserPrincipal(u.user(), u.roles()));
    }

    private Mono<UserWithRolesDTO> getUserWithRoles(String email) {
        return usersRepo.findByEmail(email)
                .flatMap(user ->
                        userRoleRepo.findAllByUserId(user.getId())
                                .flatMap(userRoles ->
                                        roleRepo.findById(userRoles.getRoleId())
                                )
                                .collectList()
                                .map(roles -> new UserWithRolesDTO(user, roles))
                );
    }
}