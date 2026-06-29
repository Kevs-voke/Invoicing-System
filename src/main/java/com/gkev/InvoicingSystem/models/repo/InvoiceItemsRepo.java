package com.gkev.InvoicingSystem.models.repo;

import com.gkev.InvoicingSystem.models.entity.InvoiceItemsEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface InvoiceItemsRepo extends ReactiveCrudRepository<InvoiceItemsEntity, UUID> {
}
