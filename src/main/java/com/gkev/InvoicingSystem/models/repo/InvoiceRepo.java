package com.gkev.InvoicingSystem.models.repo;

import com.gkev.InvoicingSystem.models.DTO.InvoiceDashboardStatsDTO;
import com.gkev.InvoicingSystem.models.entity.InvoicesEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface InvoiceRepo extends ReactiveCrudRepository<InvoicesEntity, UUID> {

    @Query(
            """
                    SELECT
   COUNT(id) FILTER (WHERE status = 'DRAFT') AS draft,
   COUNT(id) FILTER (WHERE status = 'PENDING') AS pending,
   COUNT(id) FILTER (WHERE status = 'OVERDUE') AS overdue,
   COALESCE(SUM((total-amount_paid)) FILTER (WHERE status = 'OVERDUE'), 0) AS amount_overdue,
   COALESCE(SUM((total-amount_paid)) FILTER (WHERE status IN ('OVERDUE', 'PENDING')), 0) AS amount_receivables
FROM invoice;
                    
                    """
    )
    Mono<InvoiceDashboardStatsDTO> getInvoiceDashboardStats();
}
