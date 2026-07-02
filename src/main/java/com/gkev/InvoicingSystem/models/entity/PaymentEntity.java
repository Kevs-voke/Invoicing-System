package com.gkev.InvoicingSystem.models.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Table("payments")
@Data
public class PaymentEntity {
    @Id
    private UUID id;
    @Column("invoice_id")
    private UUID invoiceId;
    @Column("customer_id")
    private UUID CustomerId;
    private BigDecimal amount;
    @Column("transaction_ref")
    private String transactionRef;
    @Column("payment_method")
    private String paymentMethod;
    private String notes;
    @Column("payment_at")
    private Timestamp paymentAt;

}
