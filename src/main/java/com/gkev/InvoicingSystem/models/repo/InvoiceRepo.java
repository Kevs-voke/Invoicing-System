package com.gkev.InvoicingSystem.models.repo;

import com.gkev.InvoicingSystem.models.DTO.*;
import com.gkev.InvoicingSystem.models.entity.InvoicesEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;
import java.time.LocalDate;
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
            COALESCE(inv.total_tax,0)  AS total_tax,
            COALESCE(inv.total,0) AS total,
            COALESCE(inv.amount_paid,0) AS amount_paid,
            COALESCE((inv.total - inv.amount_paid),0) AS balances,
            
      COALESCE(json_agg(
    json_build_object(
        'itemName', items.item_name,
        'unitPrice', items.unit_price,
        'quantity', items.quantity,
        'tax', items.tax,
        'tax_total', COALESCE(items.tax_subtotal, 0),
        'total', COALESCE(items.sub_total, 0)
    )
), '[]'::json) AS invoice_items
            
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

   
    @Query("""
    UPDATE invoice
    SET amount_paid = COALESCE(amount_paid, 0) + :amount
    WHERE id = :invoiceId
    """)
    Mono<Integer> incrementAmountPaid(UUID invoiceId, BigDecimal amount);

    @Query("""
            
            SELECT\s
                TO_CHAR(
                    date_trunc('month', CURRENT_DATE) - INTERVAL '1 month',
                    'FMMonth'
                ) AS report_period,
                NOW()::date AS generated_on,
            	agg.total_revenue,
                agg.outstanding_total,
            	 agg.current_total,
                agg.overdue_count,
                agg.current_count,
                COALESCE(tc.customers, '[]'::json) AS top_customers
            FROM (
                SELECT
            		COALESCE(SUM(inv.amount_paid),0) AS total_revenue,
                    COALESCE(SUM(inv.total) FILTER (WHERE LOWER(inv.status) IN ('overdue', 'pending')), 0) AS outstanding_total,
                    COALESCE(COUNT(inv.id) FILTER (WHERE LOWER(inv.status) IN ('overdue', 'pending')), 0) AS overdue_count,
                    COALESCE(SUM(inv.total) FILTER (WHERE LOWER(inv.status) = 'pending'), 0) AS current_total,
                    COALESCE(COUNT(inv.id) FILTER (WHERE LOWER(inv.status) = 'pending'), 0) AS current_count
                FROM invoice inv
                WHERE inv.created_at >= date_trunc('month', CURRENT_DATE)) - INTERVAL '1 month'
                  AND inv.created_at <  date_trunc('month', CURRENT_DATE))
            ) agg
            CROSS JOIN (
                SELECT json_agg(
                    json_build_object(
                        'customer_no', customer_no,
                        'customer_name', customer_name,
                        'customer_value', total_value,
                        'invoice_count', invoice_count
                    ) ORDER BY total_value DESC
                ) AS customers
                FROM (
                    SELECT\s
                        usr.user_no AS customer_no,
                        COALESCE(usr.first_name, '') || ' ' || COALESCE(usr.last_name, '') AS customer_name,
                        COALESCE(SUM(inv.total), 0) AS total_value,
                        COALESCE(COUNT(inv.id), 0) AS invoice_count
                    FROM users usr
                    LEFT JOIN invoice inv\s
                        ON usr.id = inv.cust_id
                        AND LOWER(inv.status) IN ('overdue', 'pending', 'paid')
                        AND inv.created_at >= date_trunc('month', CURRENT_DATE)) - INTERVAL '1 month'
                        AND inv.created_at <  date_trunc('month', CURRENT_DATE))
                    GROUP BY usr.user_no, usr.first_name, usr.last_name
                    ORDER BY total_value DESC
                    LIMIT 5
                ) sub
            ) tc
            CROSS JOIN (VALUES(1)) AS dummy(x);
            """)
    Mono<InvoiceSummaryReportDb>  getInvoiceMonthlySummaryReport( );


}
