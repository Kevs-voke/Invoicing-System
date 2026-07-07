package com.gkev.InvoicingSystem.models.Mapper;

import com.gkev.InvoicingSystem.models.DTO.PaymentResDTO;
import com.gkev.InvoicingSystem.models.entity.PaymentEntity;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentResDTO topaymentResDTO(PaymentEntity paymentEntity, Long customerNo, Long invoiceNo) {
        return new PaymentResDTO(
            paymentEntity.getId(),
               customerNo,
                invoiceNo,
                paymentEntity.getAmount(),
                paymentEntity.getTransactionRef(),
                paymentEntity.getPaymentMethod(),
                paymentEntity.getNotes(),
                paymentEntity.getStatus(),
                paymentEntity.getPaymentAt().toLocalDateTime()
        );
    }
}
