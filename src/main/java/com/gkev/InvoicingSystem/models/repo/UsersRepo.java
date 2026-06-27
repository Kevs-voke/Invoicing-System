package com.gkev.InvoicingSystem.models.repo;

import com.gkev.InvoicingSystem.models.DTO.CustDashboardStatsDTO;
import com.gkev.InvoicingSystem.models.entity.UsersEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;


import java.util.UUID;

public interface UsersRepo extends ReactiveCrudRepository<UsersEntity, UUID> {
    Mono<UsersEntity> findByEmail(String email);
    Mono<Boolean> existsByEmail(String email);

    @Query("""
    SELECT
        (
            SELECT COUNT(uw.user_id)
            FROM user_with_roles uw
            JOIN roles r ON r.id = uw.role_id
            WHERE r.role_name = 'CUSTOMER'
        ) AS totalCustomers,

        (
            SELECT COUNT(*)
            FROM users
            WHERE created_at::DATE = CURRENT_DATE
        ) AS newCustomers,

        (
            SELECT COALESCE(SUM(total - amount_paid), 0)
            FROM invoice
            WHERE status IN ('PENDING', 'OVERDUE')
        ) AS totalReceivables,

        (
            SELECT COALESCE(SUM(total - amount_paid), 0)
            FROM invoice
            WHERE status = 'OVERDUE'
        ) AS totalOverdue

    FROM (VALUES(1)) AS dummy(x)
""")
    Mono<CustDashboardStatsDTO> getDashboardStats();
}
