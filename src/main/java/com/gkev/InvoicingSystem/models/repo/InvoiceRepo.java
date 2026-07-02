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
                        COUNT(id) FILTER (WHERE status = 'draft') AS draft,
                        COUNT(id) FILTER (WHERE status = 'COMPLETED') AS pending,
                        COUNT(id) FILTER (WHERE status = 'WAITING') AS overdue,
                    	SUM((total-amount_paid)) FILTER (WHERE status = 'overdue') AS amount_overdue,
                    	SUM((total-amount_paid)) FILTER (WHERE status = 'overdue' || 'pending') AS  amount_receivables
                    	FROM invoice;
                    """
    )
    Mono<InvoiceDashboardStatsDTO> getInvoiceDashboardStats();
}
