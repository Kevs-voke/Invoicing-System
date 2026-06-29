package com.gkev.InvoicingSystem.models.repo;

import com.gkev.InvoicingSystem.models.entity.InvoicesEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface InvoiceRepo extends ReactiveCrudRepository<InvoicesEntity, UUID> {
}
