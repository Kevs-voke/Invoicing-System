package com.gkev.InvoicingSystem.Service;

import com.gkev.InvoicingSystem.Exceptions.UserException;
import com.gkev.InvoicingSystem.models.DTO.LoginResApiDTO;
import com.gkev.InvoicingSystem.models.DTO.LoginResponseDTO;
import com.gkev.InvoicingSystem.models.DTO.CusRegDTO;
import com.gkev.InvoicingSystem.models.DTO.LoginReqDTO;
import com.gkev.InvoicingSystem.models.Mapper.CusRegMapper;
import com.gkev.InvoicingSystem.models.entity.RolesEntity;
import com.gkev.InvoicingSystem.models.entity.UserWithRolesEntity;
import com.gkev.InvoicingSystem.models.entity.UsersEntity;
import com.gkev.InvoicingSystem.models.repo.RolesRepo;
import com.gkev.InvoicingSystem.models.repo.UserRolesRepo;
import com.gkev.InvoicingSystem.models.repo.UsersRepo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final static Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UsersRepo usersRepo;
    private final RolesRepo rolesRepo;
    private final UserRolesRepo userRolesRepo;
    private  final JwtService jwt;
    private final TransactionalOperator transactionalOperator;
    private final CusRegMapper cusRegMapper;
    private final PasswordEncoder passwordEncoder;
    private final MyUserDetailsService myUserDetailsService;


    public Mono<LoginResponseDTO> registerCust(CusRegDTO cusRegDTO) {
        logger.info("Registering new CUSTOMER: {} has started ", cusRegDTO.email());

        return transactionalOperator.transactional(
                validateEmail(cusRegDTO.email())
                        .then(Mono.defer(() -> {
                            UsersEntity user = cusRegMapper.toUserEntity(cusRegDTO);
                            user.setPassword(passwordEncoder.encode(cusRegDTO.password()));
                            return usersRepo.save(user);
                        })
                                .flatMap(savedUser ->
                                        saveRoles(savedUser.getId(), List.of("CUSTOMER"), savedUser.getEmail())
                                                .map(savedRoles -> Tuples.of(savedUser, savedRoles))
                                )
                                .flatMap(userWithRoles -> {
                                 String jwtToken = jwt.generateToken(userWithRoles.getT1(), userWithRoles.getT2());
                                    return Mono.just(new LoginResponseDTO(userWithRoles.getT1().getEmail(), userWithRoles.getT2(),jwtToken))
                                            .doOnSuccess(response -> logger.info("User: {} successfully registered",
                                                    userWithRoles.getT1().getEmail()));
                                })));


    }
    private Mono<Void> validateEmail(String email) {
        logger.info("Validating email: {}", email);
        return usersRepo.existsByEmail(email)
                .flatMap(emailExists -> {
                    if (emailExists) {
                        return Mono.error(()-> new UserException("User " + email + " Already Exists", "USER_EXISTS"));
                    }
                    logger.info("Email is valid to be registered with: {}", email);
                    return Mono.empty();
                });
    }

    private Mono<List<String>> saveRoles (UUID userId, Iterable<String> roles, String email) {
        logger.info("Saving roles for user: {}", email);
        return Flux.fromIterable(roles)
                .flatMap(roleName ->
                        rolesRepo.findByRoleName(roleName)
                                .switchIfEmpty(Mono.error(() -> new UserException("You have entered incorrect details", "INCORRECT_DETAILS")))
                                .map(RolesEntity::getId)
                                .map(roleId -> {
                                    UserWithRolesEntity entity = new UserWithRolesEntity();
                                    entity.setUserId(userId);
                                    entity.setRoleId(roleId);
                                    return entity;
                                })
                                .flatMap(userRolesRepo::save)
                )
                .collectList()
                .doOnSuccess(saved -> logger.info("Saved roles for user: {}", email))
                .thenReturn((List<String>) roles);
    }

    public Mono<LoginResponseDTO> loginCust(LoginReqDTO loginReqDTO) {
        logger.info("Login attempt for  customer: {} has started ", loginReqDTO.email());
        return usersRepo.findByEmail(loginReqDTO.email())
                .switchIfEmpty(Mono.error(() -> new UserException("Invalid email or password", "INVALID_CREDENTIALS")))
                .flatMap(user -> {
                    boolean passwordMatches = passwordEncoder.matches(loginReqDTO.password(), user.getPassword());
                    if (!passwordMatches) {
                        return Mono.error(() -> new UserException("Invalid email or password", "INVALID_CREDENTIALS"));
                    }
                    return myUserDetailsService.findRolesByemail(user.getEmail())
                            .map(roles ->
                                    roles.stream()
                                            .map(RolesEntity::getRoleName)
                                            .toList()
                            )
                            .map(roleNames -> {
                                String jwtToken = jwt.generateToken(user, roleNames);

                                return new LoginResponseDTO(
                                        user.getEmail(),
                                        roleNames,
                                        jwtToken

                                );

                            })
                            .doOnSuccess(response -> logger.info("User: {} successfully logged in", loginReqDTO.email()))
                            .doOnError(response -> logger.info("User: {} failed to login", loginReqDTO.email()));

                });
    }
}
