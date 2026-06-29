package com.gkev.InvoicingSystem.models.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Table("invoice_items")
@Data
public class InvoiceItemsEntity {
    @Id
    private UUID id;
    @Column("invoice_id")
    private UUID invoiceId;
    @Column("customer_id")
    private UUID customerId;
    @Column("item_name")
    private String itemName;
    @Column("unit_price")
    private BigDecimal unitPrice;
    private BigDecimal quantity;
    private BigDecimal tax;
    @Column("tax_subtotal")
    private BigDecimal taxSubtotal;
    @Column("sub_total")
    private BigDecimal subTotal;
}
