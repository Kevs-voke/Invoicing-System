package com.gkev.InvoicingSystem.models.repo;


import com.gkev.InvoicingSystem.models.entity.RolesEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface RolesRepo extends ReactiveCrudRepository<RolesEntity, Long> {

    Flux<RolesEntity> findByRoleName(String roleName);
}