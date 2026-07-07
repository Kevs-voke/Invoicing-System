package com.gkev.InvoicingSystem.models.repo;

import com.gkev.InvoicingSystem.models.entity.SchedulerConfigEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;


    public interface SchedulerConfigRepo extends ReactiveCrudRepository<SchedulerConfigEntity, Long> {

        Mono<SchedulerConfigEntity> findByJobName(String jobName);
    }
