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
                                       COUNT(id) FILTER (WHERE status = 'pending') AS pending,
                                       COUNT(id) FILTER (WHERE status = 'overdue') AS overdue,
                                   	COALESCE (SUM((total-amount_paid)) FILTER (WHERE status = 'overdue'), 0) AS amount_overdue,
                                   	COALESCE SUM(total - amount_paid) FILTER (WHERE status IN ('overdue', 'pending')), 0) AS  amount_receivables
                                   	FROM invoice;
                    
                    """
    )
    Mono<InvoiceDashboardStatsDTO> getInvoiceDashboardStats();
    @Query("""
            SELECT id
             FROM invoice
              WHERE invoice_no = :invoiceNo;
            """)
    Mono<UUID> getInvoiceIdByInvoiceNo(long invoiceNo);

    @Query("")
    Mono<Boolean> InvoiceExistsByUserId(UUID userId);
}
