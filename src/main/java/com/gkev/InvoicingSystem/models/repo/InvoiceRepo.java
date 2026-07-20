package com.gkev.InvoicingSystem.models.repo;

import com.gkev.InvoicingSystem.models.DTO.*;
import com.gkev.InvoicingSystem.models.entity.InvoicesEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
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
              WHERE id = :invoiceId
               AND cust_id = :userId)
                AS invoice_exists;
        """)
    Mono<Boolean> invoiceExistsForCustomer(UUID invoiceId, UUID userId);

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
                    'item_name', items.item_name,
                    'unit_price', items.unit_price,
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
            SELECT
                                         TO_CHAR(
                                             date_trunc('month', CURRENT_DATE) - INTERVAL '1 month',
                                             'FMMonth YYYY'
                                         ) AS report_period,
            
                                         NOW()::date AS generated_on,
            
                                         COALESCE(agg.total_revenue, 0) AS total_revenue,
                                         COALESCE(agg.outstanding_total, 0) AS outstanding_total,
                                         COALESCE(agg.current_total, 0) AS current_total,
                                         COALESCE(agg.overdue_total, 0) AS overdue_total,
                                         COALESCE(agg.outstanding_count, 0) AS outstanding_count,
                                         COALESCE(agg.current_count, 0) AS current_count,
                                         COALESCE(agg.overdue_count, 0) AS overdue_count,
            
                                         COALESCE(tc.top_customer_records, '[]'::json) AS top_customer_records
            
                                     FROM (
                                         SELECT
                                             COALESCE(SUM(p.amount), 0) AS total_revenue,
            
                                             COALESCE(SUM(inv.total) FILTER (WHERE LOWER(inv.status) IN ('overdue', 'pending')), 0)
                                                 AS outstanding_total,
            
                                             COALESCE(SUM(inv.total) FILTER (WHERE LOWER(inv.status) = 'pending'), 0)
                                                 AS current_total,
            
                                             COALESCE(SUM(inv.total) FILTER (WHERE LOWER(inv.status) = 'overdue'), 0)
                                                 AS overdue_total,
            
                                             COALESCE(COUNT(DISTINCT inv.id) FILTER (WHERE LOWER(inv.status) IN ('overdue', 'pending')), 0)
                                                 AS outstanding_count,
            
                                             COALESCE(COUNT(DISTINCT inv.id) FILTER (WHERE LOWER(inv.status) = 'pending'), 0)
                                                 AS current_count,
            
                                             COALESCE(COUNT(DISTINCT inv.id) FILTER (WHERE LOWER(inv.status) = 'overdue'), 0)
                                                 AS overdue_count
            
                                         FROM payments p
                                         JOIN invoice inv ON inv.id = p.invoice_id
                                         WHERE p.payment_at >= date_trunc('month', CURRENT_DATE) - INTERVAL '1 month'
                                           AND p.payment_at <  date_trunc('month', CURRENT_DATE)
                                            AND p.status = 'confirmed'
                                     ) agg
                                     CROSS JOIN (
                                         SELECT json_agg(
                                             json_build_object(
                                                 'customer_no', customer_no,
                                                 'name', customer_name,
                                                 'invoice_count', invoice_count,
                                                 'total_value', total_value
                                             )
                                         ) AS top_customer_records
                                         FROM (
                                             SELECT
                                                 usr.user_no AS customer_no,
                                                 COALESCE(usr.first_name, '') || ' ' || COALESCE(usr.last_name, '') AS customer_name,
                                                 COALESCE(SUM(p.amount), 0) AS total_value,
                                                 COALESCE(COUNT(DISTINCT inv.id), 0) AS invoice_count
                                             FROM users usr
                                             INNER JOIN invoice inv
                                                 ON usr.id = inv.cust_id
                                                 AND LOWER(inv.status) IN ('overdue', 'pending', 'paid')
                                             INNER JOIN payments p
                                                 ON p.invoice_id = inv.id
                                                 AND p.payment_at >= date_trunc('month', CURRENT_DATE) - INTERVAL '1 month'
                                                 AND p.payment_at <  date_trunc('month', CURRENT_DATE)
                                             GROUP BY usr.user_no, usr.first_name, usr.last_name
                                             ORDER BY total_value DESC
                                             LIMIT 5
                                         ) sub
                                     ) tc;
            """)
    Mono<InvoiceSummaryReportDb> getInvoiceMonthlySummaryReport();

    @Query("""
            SELECT status\s
            FROM invoice
            WHERE invoice_no = :invoiceNo;
            """)
    Mono<String> getInvoiceStatus(long invoiceNo);

    @Query("""
            UPDATE status
            FROM invoice
            WHERE invoice_no = :invoiceNo
            """)
    Mono<Void> updateInvoiceStatus(long invoiceNo);

    @Query("""
            SELECT
            COALESCE(usr.first_name, '') || ' ' || COALESCE(usr.last_name, '') AS customer_name,
            usr.email,
            inv.invoice_no,
            COALESCE(inv.total_tax,0),
            COALESCE(inv.total,0),
            inv.due_date,
            COALESCE(
            json_agg(
            json_build_object(
            "item_name", items.item_name,
            "unit_price", COALESCE(items.unit_price,0.0),
            "quantity", COALESCE(items.quantity,0),
            "tax", COALESCE(items.tax,0.0),
            "tax_subtotal", COALESCE(items.tax_subtotal,0),
            "sub_total", COALESCE(items.sub_total,0)
            )
            ), '[]'::json) AS invoice_items
            
            FROM invoice inv
            INNER JOIN users usr ON inv.cust_id = usr.id
            LEFT JOIN invoice_items items ON items.invoice_id = inv.id
            WHERE inv.invoice_no = 0
            GROUP BY usr.first_name, usr.last_name, inv.invoice_no, inv.total_tax,inv.due_date,inv.total,usr.email;
            """)
    Mono<InvoiceConfirmationDTO> getConfirmInvoice(long invoiceNo);

}