package com.gkev.InvoicingSystem.models.DTO;

import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;

public interface CustDashboardStatsDTO {
    @Value("#{target.total_customers}")
    int totalCustomers();

    @Value("#{target.new_customers}")
    int newCustomers();

    @Value("#{target.total_receivables}")
    BigDecimal totalReceivables();

    @Value("#{target.total_overdue}")
    BigDecimal totalOverdue();
}