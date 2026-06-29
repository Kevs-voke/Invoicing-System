package com.gkev.InvoicingSystem.models.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Table("invoice")
@Data
public class InvoicesEntity {
    @Id
    private UUID id;
    @Column("invoice_no")
    private Long invoiceNo;

    private String status;
    @Column("created_at")
    private Timestamp createdAt;
    @Column("due_date")
    private Timestamp dueDate;
    @Column("total_tax")
    private BigDecimal totalTax;
    private BigDecimal total;
    @Column("cust_id")
    private UUID customerId;

}



