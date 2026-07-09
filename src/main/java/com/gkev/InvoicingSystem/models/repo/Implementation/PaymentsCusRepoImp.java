package com.gkev.InvoicingSystem.models.repo.Implementation;

import com.gkev.InvoicingSystem.models.DTO.PaymentCustResDTO;
import com.gkev.InvoicingSystem.models.DTO.PaymentsFilterDTO;
import com.gkev.InvoicingSystem.models.Enums.PaymentSortBy;
import com.gkev.InvoicingSystem.models.repo.PaymentsCusRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PaymentsCusRepoImp implements PaymentsCusRepo {

    private final DatabaseClient client;

    @Override
    public Flux<PaymentCustResDTO> getPayments(PaymentsFilterDTO filter, int page, int size) {
        StringBuilder sql = new StringBuilder("""
        SELECT
            p.payment_no,
            p.id,
            us.first_name,
            us.last_name,
            us.user_no,
            inv.invoice_no,
            p.amount,
            p.payment_method,
            p.transaction_ref,
            p.status,
            p.payment_at
        FROM payments p
        LEFT JOIN users us ON p.customer_id = us.id
        LEFT JOIN invoice inv ON p.invoice_id = inv.id
        WHERE 1=1
        """);

        Map<String, Object> params = new HashMap<>();
        if (filter.hasCustomerNo()) {
            sql.append(" AND us.user_no = :customerNo");
            params.put("customerNo", filter.customerNo());
        }
        if (filter.hasInvoiceNo()) {
            sql.append(" AND inv.invoice_no = :invoiceNo");
            params.put("invoiceNo", filter.invoiceNo());
        }
        if (filter.hasPaymentNo()) {
            sql.append(" AND p.payment_no = :paymentNo");
            params.put("paymentNo", filter.paymentNo());
        }
        if (filter.hasFirstName()) {
            sql.append(" AND us.first_name = :firstName");
            params.put("firstName", filter.firstName());
        }
        if (filter.hasLastName()) {
            sql.append(" AND us.last_name = :lastName");
            params.put("lastName", filter.lastName());
        }
        if (filter.hasPaymentMethod()) {
            sql.append(" AND p.payment_method = :paymentMethod");
            params.put("paymentMethod", filter.paymentMethod());
        }
        if (filter.hasStatus()) {
            sql.append(" AND p.status = :status");
            params.put("status", filter.status());
        }
        if (filter.hasPaymentDateFrom()) {
            sql.append(" AND p.payment_at >= :paymentDateFrom");
            params.put("paymentDateFrom", filter.paymentDateFrom());
        }
        if (filter.hasPaymentDateTo()) {
            sql.append(" AND p.payment_at <= :paymentDateTo");
            params.put("paymentDateTo", filter.paymentDateTo());
        }

        String sortColumn = resolveSortColumn(filter.sortBy());
        String direction = filter.sortDirection() == Sort.Direction.DESC ? "DESC" : "ASC";
        sql.append(" ORDER BY ").append(sortColumn).append(" ").append(direction);

        sql.append(" LIMIT :limit OFFSET :offset");
        params.put("limit", size);
        params.put("offset", (long) page * size);

        var spec = client.sql(sql.toString());
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            spec = spec.bind(entry.getKey(), entry.getValue());
        }

        return spec.map(
            (row, meta) -> PaymentCustResDTO.builder()
                    .paymentNo(row.get("payment_no", Long.class))
                    .id(row.get("id", UUID.class))
                    .customerNo(row.get("user_no", Long.class))
                    .firstName(row.get("first_name", String.class))
                    .lastName(row.get("last_name", String.class))
                    .invoiceNo(row.get("invoice_no", Long.class))
                    .amount(row.get("amount", BigDecimal.class))
                    .paymentMethod(row.get("payment_method", String.class))
                    .status(row.get("status", String.class))
                    .transactionRef(row.get("transaction_ref", String.class))
                    .paymentAt(Objects.requireNonNull(row.get("payment_at", LocalDateTime.class)))
                    .build()
        ).all();
    }
    private String resolveSortColumn(PaymentSortBy sortBy) {
        return switch (sortBy) {
            case PAYMENT_DATE -> "p.payment_at";
            case AMOUNT -> "p.amount";
            case CUSTOMER_NO -> "us.user_no";
            case INVOICE_NO -> "inv.invoice_no";
            case PAYMENT_METHOD -> "p.payment_method";
        };
    }
    }