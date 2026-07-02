package com.gkev.InvoicingSystem.models.Mapper;

import com.gkev.InvoicingSystem.models.DTO.InvoiceItemsResDTO;
import com.gkev.InvoicingSystem.models.DTO.InvoiceRespDTO;
import com.gkev.InvoicingSystem.models.entity.InvoicesEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InvoiceMapper {
  public InvoiceRespDTO toInvoiceDTO(InvoicesEntity invoiceEntity, List<InvoiceItemsResDTO>  invoiceItemsDTO, long customerNo) {
      return new InvoiceRespDTO(
        invoiceEntity.getInvoiceNo(),
        invoiceEntity.getStatus(),
        customerNo,
        invoiceEntity.getCreatedAt().toLocalDateTime().toLocalDate(),
        invoiceEntity.getDueDate().toLocalDateTime().toLocalDate(),
        invoiceItemsDTO,
        invoiceEntity.getTotalTax(),
        invoiceEntity.getTotal()
);
  }

}

