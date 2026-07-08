package com.gkev.InvoicingSystem.models.repo;

import com.gkev.InvoicingSystem.models.DTO.DetailedPaymentResDTO;
import com.gkev.InvoicingSystem.models.DTO.PaymentDashboardStatsDTO;
import com.gkev.InvoicingSystem.models.entity.PaymentEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface PaymentsRepo extends ReactiveCrudRepository<PaymentEntity, UUID> {

    @Query("SELECT id FROM payments WHERE payment_no = :paymentNo")
    Mono<UUID> getPaymentIdByPaymentNo(Long paymentNo);

    @Query("""
        SELECT
            p.payment_no,
            us.user_no          AS customer_no,
            us.first_name,
            us.last_name,
            us.email,
            us.phone_number,
            inv.invoice_no,
            inv.status           AS invoice_status,
            inv.created_at       AS invoice_created_at,
            inv.due_date         AS invoice_due_date,
            COALESCE(inv.total, 0)                    AS invoice_total,
            COALESCE(inv.total - inv.amount_paid, 0)  AS invoice_balance,
            p.amount,
            p.payment_method,
            p.transaction_ref,
            p.notes,
            p.status,
            p.payment_at,
            p.created_at,
            p.confirmed_at,
            conf.first_name       AS confirmed_by_first_name,
            conf.last_name        AS confirmed_by_last_name,
            p.failed_at,
            fail_u.first_name     AS failed_by_first_name,
            fail_u.last_name      AS failed_by_last_name
        FROM payments p
        JOIN users us              ON p.customer_id = us.id
        JOIN invoice inv           ON p.invoice_id = inv.id
        LEFT JOIN users conf       ON p.confirmed_by = conf.id
        LEFT JOIN users fail_u     ON p.failed_by = fail_u.id
        WHERE p.payment_no = :paymentNo;
        """)
    Mono<DetailedPaymentResDTO> getDetailedPaymentByPaymentNo(Long paymentNo);

    @Query("""
        SELECT
            COUNT(id) FILTER (WHERE LOWER(status) = 'pending') AS pending_count,
            COALESCE(SUM(amount) FILTER (WHERE LOWER(status) = 'pending'), 0) AS pending_amount,
            COUNT(id) FILTER (WHERE LOWER(status) = 'confirmed') AS confirmed_count,
            COALESCE(SUM(amount) FILTER (WHERE LOWER(status) = 'confirmed'), 0) AS confirmed_amount,
            COUNT(id) FILTER (WHERE LOWER(status) = 'failed') AS failed_count,
            COALESCE(SUM(amount) FILTER (WHERE LOWER(status) = 'failed'), 0) AS failed_amount
        FROM payments;
        """)
    Mono<PaymentDashboardStatsDTO> getPaymentDashboardStats();
}