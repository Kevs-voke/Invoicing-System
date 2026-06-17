package com.gkev.InvoicingSystem.models.repo;

import com.gkev.InvoicingSystem.models.entity.UserWithRolesEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface UserRolesRepo extends ReactiveCrudRepository<UserWithRolesEntity,Long > {
    Flux<UserWithRolesEntity> findAllByUserId(UUID id);
}
