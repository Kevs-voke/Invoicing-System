package com.gkev.InvoicingSystem.models.DTO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.relational.core.mapping.Column;
import java.math.BigDecimal;

public record CustDashboardStatsDTO(

    @Column("total_customers")
    Integer totalCustomers,

    @Column("new_customers")
    Integer newCustomers,

    @Column("total_receivables")
    BigDecimal totalReceivables,

    @Column("total_overdue")
    BigDecimal totalOverdue
) {}