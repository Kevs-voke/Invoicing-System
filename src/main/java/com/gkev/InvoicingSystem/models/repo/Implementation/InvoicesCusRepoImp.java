package com.gkev.InvoicingSystem.models.repo.Implementation;

import com.gkev.InvoicingSystem.models.DTO.InvoiceCustResDTO;
import com.gkev.InvoicingSystem.models.DTO.InvoicesFilterDTO;
import com.gkev.InvoicingSystem.models.Enums.InvoiceSortBy;
import com.gkev.InvoicingSystem.models.repo.InvoicesCusRepo;
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

@Repository
@RequiredArgsConstructor
public class InvoicesCusRepoImp implements InvoicesCusRepo {

    private final DatabaseClient client;


    @Override
    public Flux<InvoiceCustResDTO> getInvoices(InvoicesFilterDTO filter, int page, int size) {
        StringBuilder sql = new StringBuilder("""
    SELECT 
     us.first_name,
     us.last_name,
     us.user_no,
     inv.invoice_no,
     inv.created_at,
     inv.due_date,
     inv.total,
     inv.amount_paid,
     inv.status
    FROM invoice inv
    LEFT JOIN users us ON inv.cust_id = us.id
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
        if (filter.hasFirstName()) {
            sql.append(" AND us.first_name = :firstName");
            params.put("firstName", filter.firstName());
        }
        if (filter.hasLastName()) {
            sql.append(" AND us.last_name = :lastName");
            params.put("lastName", filter.lastName());
        }
        if (filter.hasDueDateFrom()) {
            sql.append(" AND inv.due_date >= :dueDateFrom");
            params.put("dueDateFrom", filter.dueDateFrom());
        }
        if (filter.hasDueDateTo()) {
            sql.append("AND inv.due_date <= :dueDateTo ");
            params.put("dueDateTo", filter.dueDateTo());
        }
        if (filter.hasStatus()) {
            sql.append(" AND inv.status = :status");
            params.put("status", filter.status());
        }
        String sortColumn = resolveSortColumn(filter.sortBy());
        String direction = filter.sortDirection() == Sort.Direction.DESC ? "DESC" : "ASC";
        sql.append(" ORDER BY ").append(sortColumn).append(" ").append(direction);

        sql.append("LIMIT :limit OFFSET :offset");
        params.put("limit", size);
        params.put("offset", (long) page * size);

        var spec = client.sql(sql.toString());
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            spec = spec.bind(entry.getKey(), entry.getValue());
        }

       return  spec.map(
               (row, meta) -> InvoiceCustResDTO.builder()
                       .customerNo(row.get("user_no", Long.class))
                       .firstName(row.get("first_name", String.class))
                       .lastName(row.get("last_name", String.class))
                       .invoiceNo(row.get("invoice_no", Long.class))
                       .status(row.get("status", String.class))
                       .createdAt(Objects.requireNonNull(row.get("created_at", LocalDateTime.class)).toLocalDate())
                       .dueDate(Objects.requireNonNull(row.get("due_date", LocalDateTime.class)).toLocalDate())
                       .amountPaid(row.get("amount_paid", BigDecimal.class))
                       .invoiceTotal(row.get("total", BigDecimal.class))
                       .build()
       )
               .all();


    }

    private String resolveSortColumn(InvoiceSortBy sortBy) {
        return switch (sortBy) {
            case DUE_DATE -> "inv.due_date";
            case CREATE_DATE -> "inv.created_at";
            case INVOICE_NO -> "inv.invoice_no";
            case FIRST_NAME -> "us.first_name";
            case LAST_NAME -> "us.last_name";
            case TOTAL -> "inv.total";
            case STATUS -> "inv.status";
        };
    }
}
