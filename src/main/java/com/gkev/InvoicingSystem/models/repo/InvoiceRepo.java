package com.gkev.InvoicingSystem.models.repo;

import com.gkev.InvoicingSystem.models.DTO.DetailedInvoiceDTO;
import com.gkev.InvoicingSystem.models.DTO.InvoiceDashboardStatsDTO;
import com.gkev.InvoicingSystem.models.DTO.OverdueInvoiceDTO;
import com.gkev.InvoicingSystem.models.entity.InvoicesEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.data.r2dbc.repository.Modifying;
import java.math.BigDecimal;

import java.util.UUID;

public interface InvoiceRepo extends ReactiveCrudRepository<InvoicesEntity, UUID> {

    @Query(
            """
                    SELECT
                                       COUNT(id) FILTER (WHERE LOWER(status) = LOWER('draft')) AS draft,
                                       COUNT(id) FILTER (WHERE LOWER(status) = LOWER('pending')) AS pending,
                                       COUNT(id) FILTER (WHERE LOWER(status) = LOWER('overdue')) AS overdue,
                                   	COALESCE (SUM((total-amount_paid)) FILTER (WHERE LOWER(status) = LOWER('overdue')), 0) AS amount_overdue,
                                   	COALESCE (SUM((total - amount_paid)) FILTER (WHERE LOWER(status) IN (LOWER('overdue'), LOWER('pending'))), 0) AS  amount_receivables
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
    @Query("""
        SELECT invoice_no
         FROM invoice
          WHERE id = :id;
        """)
Mono<Long> getInvoiceNoByInvoiceId(UUID id);

    @Query("""
            SELECT EXISTS (
                SELECT 1
                 FROM invoice
                  WHERE cust_id =  :userId)
                   AS invoice_exists;
            """)
    Mono<Boolean> invoiceExistsByUserId(UUID userId);

    @Query("""
            SELECT
            inv.invoice_no,
            inv.status,
            inv.due_date,
            COALESCE(inv.total_tax,0),
            COALESCE(inv.total,0),
            COALESCE(inv.amount_paid,0),
            COALESCE((inv.total - inv.amount_paid),0) AS balances,
            
            COALESCE(json_agg(
            json_build_object(
            'item_name',items.item_name,
            'unit_price', items.unit_price,
            'quantity', items.quantity,
            'total_tax', COALESCE(items.tax_subtotal,0),
            'sub_total', COALESCE(items.sub_total,0)
            )
            ),'[]'::json) AS invoice_items
            
            FROM invoice inv
            JOIN invoice_items items ON inv.id =items.invoice_id
            WHERE inv.id = :id
            GROUP BY inv.invoice_no,
            inv.status,
            inv.due_date,
            inv.total_tax,
            inv.total,
            inv.amount_paid;
            
            
            
            """)
    Mono<DetailedInvoiceDTO> getDetailedInvoiceById(UUID id);

    @Query("""
            SELECT 
                 us.first_name,
                 us.last_name,
                 us.user_no,
                 us.email,
                 us.phone_number,
                 inv.invoice_no,
                 inv.created_at,
                 inv.due_date,
                 inv.total,
                 inv.amount_paid,
                 (inv.total - amount_paid) AS overdue
                FROM invoice inv
                LEFT JOIN users us ON inv.cust_id = us.id
                WHERE inv.status = 'overdue'
            """)
    Flux<OverdueInvoiceDTO> getOverdueInvoiceCust();
}
