package com.gkev.InvoicingSystem.models.repo.Implementation;

import com.gkev.InvoicingSystem.models.DTO.CusFilterDTO;
import com.gkev.InvoicingSystem.models.DTO.CustomerDetailResDTO;
import com.gkev.InvoicingSystem.models.DTO.CustomerInvoiceResDTO;
import com.gkev.InvoicingSystem.models.repo.CustomerRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class CustomerRepoImp implements CustomerRepo {

    private final DatabaseClient client;

    @Override
    public Flux<CustomerInvoiceResDTO> findCustomers(CusFilterDTO filter, int page, int size) {
        StringBuilder sql = new StringBuilder("""
                
                SELECT
                    usr.user_no,
                    usr.email,
                    usr.phone_number,
                    inv.invoice_no,
                    inv.status,
                    (inv.total - inv.amount_paid) AS total,
                    inv.due_date
                FROM users usr
                INNER JOIN user_with_roles uw ON uw.user_id = usr.id
                INNER JOIN roles r ON r.id = uw.role_id
                LEFT JOIN invoice inv ON inv.cust_id = usr.id
                WHERE r.role_name = 'CUSTOMER'
                AND 1=1
                
                """);
        Map<String, Object> params = new HashMap<>();
        if (filter.hasEmail()) {
    sql.append(" AND usr.email ILIKE :email ");
    params.put("email", "%" + filter.email() + "%");
}
if (filter.hasPhoneNumber()) {
    sql.append(" AND usr.phone_number LIKE :phoneNumber ");
    params.put("phoneNumber", "%" + filter.phoneNumber() + "%");
}
if (filter.hasCustomerNo()) {
    sql.append(" AND CAST(usr.user_no AS TEXT) LIKE :customerNo ");
    params.put("customerNo", "%" + filter.customerNo() + "%");
}
        if (filter.hasStatus()){
            sql.append("AND inv.status = :status");
            params.put("status", filter.status());
        }
        if (filter.hasDueDateFrom()) {
            sql.append("AND inv.due_date >= :dueDateFrom ");
            params.put("dueDateFrom", filter.dueDateFrom());
        }
        if (filter.hasDueDateTo()) {
            sql.append("AND inv.due_date <= :dueDateTo ");
            params.put("dueDateTo", filter.dueDateTo());
        }
        if (filter.hasInvoiceNo()){
            sql.append("AND inv.invoice_no = :invoiceNo");
            params.put("invoiceNo", filter.invoiceNo());
        }

        sql.append("""
                 ORDER BY
                CASE
                    WHEN inv.status = 'overdue' THEN 1
                    WHEN inv.status = 'pending' THEN 2
                    WHEN inv.status = 'paid'    THEN 3
                    ELSE 4
                END,
                inv.due_date %s
                """.formatted(filter.dueDateSortDirection().name())
        );
        sql.append("LIMIT :limit OFFSET :offset");
        params.put("limit", size);
        params.put("offset", (long)page * size);

        var spec = client.sql(sql.toString());
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            spec = spec.bind(entry.getKey(), entry.getValue());
        }

        return spec.map(
                (row, meta) -> CustomerInvoiceResDTO.builder()
                        .userNo(row.get("user_no", String.class))
                        .email(row.get("email", String.class))
                        .phoneNumber(row.get("phone_number", String.class))
                        .invoiceNo(row.get("invoice_no", Long.class))
                        .status(row.get("status", String.class))
                        .total(row.get("total", BigDecimal.class))
                        .dueDate(row.get("due_date", LocalDate.class))
                        .build())
                .all();
    }

    @Override
    public Mono<CustomerDetailResDTO> findByUserNo(Long userNo) {
        String sql = """
                SELECT
                    usr.user_no,
                    usr.first_name,
                    usr.last_name,
                    usr.email,
                    usr.phone_number
                FROM users usr
                INNER JOIN user_with_roles uw ON uw.user_id = usr.id
                INNER JOIN roles r ON r.id = uw.role_id
                WHERE r.role_name = 'CUSTOMER'
                AND usr.user_no = :userNo
                """;

        return client.sql(sql)
                .bind("userNo", userNo)
                .map((row, meta) -> CustomerDetailResDTO.builder()
                        .userNo(row.get("user_no", Long.class))
                        .firstName(row.get("first_name", String.class))
                        .lastName(row.get("last_name", String.class))
                        .email(row.get("email", String.class))
                        .phoneNumber(row.get("phone_number", String.class))
                        .build())
                .one();
    }

}