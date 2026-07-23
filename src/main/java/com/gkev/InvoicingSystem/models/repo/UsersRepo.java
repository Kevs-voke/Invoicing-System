package com.gkev.InvoicingSystem.models.repo;

import com.gkev.InvoicingSystem.models.DTO.CustDashboardStatsDTO;
import com.gkev.InvoicingSystem.models.entity.UsersEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import java.util.UUID;

public interface UsersRepo extends ReactiveCrudRepository<UsersEntity, UUID> {
    Mono<UsersEntity> findByEmail(String email);
    Mono<UsersEntity> findByResetPasswordToken(String token);
    Mono<Boolean> existsByEmail(String email);

   @Query("""
    SELECT
        (SELECT COUNT(uw.user_id)
         FROM user_with_roles uw
         JOIN roles r ON r.id = uw.role_id
         WHERE r.role_name = 'CUSTOMER') AS total_customers,

        (SELECT COUNT(u.id)
        FROM users u
        JOIN user_with_roles uw ON uw.user_id = u.id
        JOIN roles r ON r.id = uw.role_id
        WHERE r.role_name = 'CUSTOMER'
        AND u.created_at::DATE = CURRENT_DATE) AS new_customers,
        
        (SELECT COALESCE(SUM(total - amount_paid), 0)
         FROM invoice
         WHERE LOWER(status) IN ('pending', 'overdue')) AS total_receivables,

        (SELECT COALESCE(SUM(total - amount_paid), 0)
         FROM invoice
         WHERE LOWER(status) = 'overdue') AS total_overdue
    FROM (VALUES(1)) AS dummy(x)
    """)
Mono<CustDashboardStatsDTO> getCustomerDashboardStats();
   @Query("""
           SELECT id
            FROM users
            WHERE user_no = :userNo
           """)
   Mono<UUID> getUserIdByUserNo(Long userNo);
   @Query("""
        SELECT user_no
         FROM users
         WHERE id = :id
        """)
Mono<Long> getUserNoByUserId(UUID id);

   @Query("""
           SELECT u.email
           FROM users u
           JOIN user_with_roles uw ON u.id = uw.user_id
           JOIN roles r ON r.id = uw.role_id
           WHERE r.role_name = 'OWNER';
           """)
   Flux<String> getBusinessOwners();
}