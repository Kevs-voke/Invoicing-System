package com.gkev.InvoicingSystem.Service;

import com.gkev.InvoicingSystem.Exceptions.UserException;
import com.gkev.InvoicingSystem.Utils.TempPasswordUtils;
import com.gkev.InvoicingSystem.models.DTO.*;
import com.gkev.InvoicingSystem.models.Mapper.CusRegMapper;
import com.gkev.InvoicingSystem.models.Mapper.MeMapper;
import com.gkev.InvoicingSystem.models.Mapper.NewAccountEmailMapper;
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
import org.thymeleaf.spring6.SpringTemplateEngine;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final static Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UsersRepo usersRepo;
    private final RolesRepo rolesRepo;
    private final UserRolesRepo userRolesRepo;
    private final JwtService jwt;
    private final TransactionalOperator transactionalOperator;
    private final CusRegMapper cusRegMapper;
    private final PasswordEncoder passwordEncoder;
    private final MyUserDetailsService myUserDetailsService;
    private final SpringTemplateEngine emailTemplateEngine;
    private final EmailServiceSender emailServiceSender;
    private final NewAccountEmailMapper newAccountEmailMapper;

    public Mono<LoginResponseDTO> CustSelfReg(CusRegDTO cusRegDTO) {
        logger.info("Registering new CUSTOMER: {} has started", cusRegDTO.email());
        List<String> roles = new ArrayList<>();
        roles.add("CUSTOMER");
        return registerUserhelper(cusRegDTO, roles)
                .flatMap(userWithRoles -> {
                    String jwtToken = jwt.generateToken(userWithRoles.getT1(), userWithRoles.getT2());
                    return Mono.just(new LoginResponseDTO(
                            userWithRoles.getT1().getFirstName(),
                            userWithRoles.getT2(),
                            jwtToken
                    )).doOnSuccess(response ->
                            logger.info("User: {} successfully registered", userWithRoles.getT1().getEmail())
                    );
                });
    }

    public Mono<String> registerUser(UserRegDTO userRegDTO) {
        logger.info("Registering new user: {} has started", userRegDTO.cusRegDTO().email());
        return registerUserhelper(userRegDTO.cusRegDTO(), userRegDTO.roles())
                .flatMap(response -> {

                            String email = response.getT1().getEmail();
                            return Mono.just(email)
                                    .doOnSuccess(userEmail -> logger.info("User {} has been registered", userEmail));

                        }
                );


    }

    public Mono<String> createCustomer(CusRegDTO cusRegDTO) {
        logger.info("Registering new customer: {} has started", cusRegDTO.email());
        List<String> roles = new ArrayList<>();
        roles.add("CUSTOMER");
        return registerUserhelper(cusRegDTO, roles)
                .flatMap(response -> {

                            String email = response.getT1().getEmail();
                            return Mono.just(email)
                                    .doOnSuccess(userEmail -> logger.info("Customer {} has been registered", userEmail));

                        }
                );


    }


    private Mono<Tuple2<UsersEntity, List<String>>> registerUserhelper(CusRegDTO cusRegDTO, List<String> roles) {
        return transactionalOperator.transactional(
                validateEmail(cusRegDTO.email())
                        .then(Mono.defer(() -> {
                            UsersEntity user = cusRegMapper.toUserEntity(cusRegDTO);
                            user.setPassword(passwordEncoder.encode(cusRegDTO.password()));
                            return usersRepo.save(user);
                        }))
                        .flatMap(savedUser ->
                                saveRoles(savedUser.getId(), roles, savedUser.getEmail())
                                        .map(savedRoles -> Tuples.of(savedUser, savedRoles))
                        )
        );
    }

    private Mono<Void> validateEmail(String email) {
        logger.info("Validating email: {}", email);
        return usersRepo.existsByEmail(email)
                .flatMap(emailExists -> {
                    if (emailExists) {
                        return Mono.error(() -> new UserException("USER_EXISTS", "User Already Exists"));
                    }
                    logger.info("Email is valid to be registered with: {}", email);
                    return Mono.empty();
                });
    }

    private Mono<List<String>> saveRoles(UUID userId, Iterable<String> roles, String email) {
        logger.info("Saving roles for user: {}", email);
        return Flux.fromIterable(roles)
                .flatMap(roleName ->
                        rolesRepo.findByRoleName(roleName)
                                .switchIfEmpty(Mono.error(() -> new UserException("INCORRECT_DETAILS", "You have entered incorrect details")))
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

    public Mono<LoginResponseDTO> loginUser(LoginReqDTO loginReqDTO) {
        logger.info("Login attempt for  customer: {} has started ", loginReqDTO.email());
        return usersRepo.findByEmail(loginReqDTO.email())
                .switchIfEmpty(Mono.error(() -> new UserException("INVALID_CREDENTIALS", "Invalid email or password")))
                .flatMap(user -> {
                    boolean passwordMatches = passwordEncoder.matches(loginReqDTO.password(), user.getPassword());
                    if (!passwordMatches) {
                        return Mono.error(() -> new UserException("INVALID_CREDENTIALS", "Invalid email or password"));
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
                                        user.getFirstName(),
                                        roleNames,
                                        jwtToken

                                );

                            })
                            .doOnSuccess(response -> logger.info("User: {} successfully logged in", loginReqDTO.email()))
                            .doOnError(response -> logger.info("User: {} failed to login", loginReqDTO.email()));

                });
    }

    public Mono<AdminCreateUserResDTO> createUserByAdmin(AdminCreateUserDTO dto) {
        logger.info("Manager creating new {} account: {}", dto.role(), dto.email());
        String tempPassword = TempPasswordUtils.generate();
        CusRegDTO cusRegDTO = new CusRegDTO(
                dto.firstName(),
                dto.lastName(),
                dto.email(),
                dto.phoneNumber(),
                tempPassword
        );
        List<String> roles = new ArrayList<>();
        roles.add(dto.role());

        return registerUserhelper(cusRegDTO, roles)
                .flatMap(savedUser ->
                        sendNewAccountEmail(savedUser.getT1(), tempPassword, dto.role())
                                .thenReturn(new AdminCreateUserResDTO(
                                        savedUser.getT1().getFirstName(),
                                        savedUser.getT1().getLastName(),
                                        savedUser.getT1().getEmail(),
                                        dto.role()
                                ))
                )
                .doOnSuccess(response -> logger.info("Account created and credentials emailed to: {}", dto.email()));
    }

    private Mono<Void> sendNewAccountEmail(UsersEntity user, String tempPassword, String role) {
        var context = newAccountEmailMapper.setData(user.getFirstName(), user.getEmail(), tempPassword, role);
        String html = emailTemplateEngine.process("NewAccountCredentials", context);
        EmailMessage message = new EmailMessage(
                user.getEmail(),
                "Your ImaraBill account has been created",
                html,
                null,
                null
        );
        return emailServiceSender.sendEmail(message);
    }

    public Mono<List<AdminUserListItemDTO>> listUsers() {
        logger.info("Listing all users with roles");
        return usersRepo.findAll()
                .flatMap(user ->
                        myUserDetailsService.findRolesByemail(user.getEmail())
                                .map(roles ->
                                        new AdminUserListItemDTO(
                                                user.getId(),
                                                user.getUserNo(),
                                                user.getFirstName(),
                                                user.getLastName(),
                                                user.getEmail(),
                                                user.getPhoneNumber(),
                                                roles.stream().map(RolesEntity::getRoleName).toList(),
                                                user.getDisabled(),
                                                user.getCreatedAt()
                                        )
                                )
                )
                .collectList();
    }

    public Mono<MeDTO> getMe(UUID userId) {
        logger.info("Querying user: {}", userId);
        return usersRepo.findById(userId)
                .switchIfEmpty(Mono.error(() -> new UserException("User not found", "USER_NOT_FOUND")))
                .flatMap(user ->
                        myUserDetailsService.findRolesByemail(user.getEmail())
                                .map(roles ->
                                        roles.stream()
                                                .map(RolesEntity::getRoleName)
                                                .toList()
                                )
                                .map(roleNames -> MeMapper.toMeDto(user, roleNames))
                )
                .doOnSuccess(response -> logger.info("User: {} has been successfully queried ", userId));
    }
}
