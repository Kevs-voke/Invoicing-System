package com.gkev.InvoicingSystem.models.repo;

import com.gkev.InvoicingSystem.models.entity.UsersEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;


import java.util.UUID;

public interface UsersRepo extends ReactiveCrudRepository<UsersEntity, UUID> {
    Mono<UsersEntity> findByEmail(String email);
    Mono<Boolean> existsByEmail(String email);
}
