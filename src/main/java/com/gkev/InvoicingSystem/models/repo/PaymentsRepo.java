package com.gkev.InvoicingSystem.models.repo;

import com.gkev.InvoicingSystem.models.entity.PaymentEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface PaymentsRepo extends ReactiveCrudRepository<PaymentEntity, UUID> {
}
