package com.gkev.InvoicingSystem.Service;

import com.gkev.InvoicingSystem.Exceptions.ResourceNotFound;
import com.gkev.InvoicingSystem.models.DTO.PaymentDTO;
import com.gkev.InvoicingSystem.models.repo.InvoiceRepo;
import com.gkev.InvoicingSystem.models.repo.PaymentsRepo;
import com.gkev.InvoicingSystem.models.repo.UsersRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentsRepo paymentsRepo;
    private final InvoiceRepo invoiceRepo;
    private final UsersRepo usersRepo;


    public Mono<Void> makePayment(PaymentDTO paymentDTO){
        Mono<UUID> invoiceId = invoiceRepo.getInvoiceIdByInvoiceNo(paymentDTO.invoiceNo());
        Mono<UUID> userId = usersRepo.getUserIdByUserNo(paymentDTO.customerNo());
        return Mono.zip(invoiceId, userId)
                .flatMap(tupple -> {
                    UUID invoiceNumber = tupple.getT1();
                    UUID userNumber = tupple.getT2();
                    if(invoiceNumber == null)
                        return Mono.error(new ResourceNotFound("NOT_FOUND", "Enter VALID Invoice Number"));

                    if (userNumber == null)
                        return Mono.error(new ResourceNotFound("NOT_FOUND", "Enter VALID User Number"));

                    Mono
                });

    }

}
