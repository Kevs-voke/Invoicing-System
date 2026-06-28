package com.gkev.InvoicingSystem.models.DTO;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public record InvoiceRespDTO(
      Long  customerNO,
      String status,
      Long invoiceNo,
      Date createdDate,
      Date  dueDate,
      List<InvoiceItemsResDTO> items,
      BigDecimal total_tax,
      BigDecimal  total


) {
}




