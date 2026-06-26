package com.gkev.InvoicingSystem.models.DTO;

import java.math.BigDecimal;

public interface CustDashboardStatsDTO {
    int totalCustomers();
    int newCustomers();
    BigDecimal totalReceivables();
    BigDecimal totalOverdue();


}
