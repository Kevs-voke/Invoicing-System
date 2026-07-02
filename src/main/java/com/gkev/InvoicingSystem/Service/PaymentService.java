package com.gkev.InvoicingSystem.Service;

import com.gkev.InvoicingSystem.Exceptions.ResourceNotFound;
import com.gkev.InvoicingSystem.models.DTO.PaymentDTO;
import com.gkev.InvoicingSystem.models.DTO.PaymentResDTO;
import com.gkev.InvoicingSystem.models.Mapper.PaymentMapper;
import com.gkev.InvoicingSystem.models.entity.PaymentEntity;
import com.gkev.InvoicingSystem.models.repo.InvoiceRepo;
import com.gkev.InvoicingSystem.models.repo.PaymentsRepo;
import com.gkev.InvoicingSystem.models.repo.UsersRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentsRepo paymentsRepo;
    private final InvoiceRepo invoiceRepo;
    private final UsersRepo usersRepo;
    private final PaymentMapper paymentMapper;


    public Mono<PaymentResDTO> makePayment(PaymentDTO paymentDTO){
        Mono<UUID> invoiceId = invoiceRepo.getInvoiceIdByInvoiceNo(paymentDTO.invoiceNo())
                .switchIfEmpty(Mono.error(new ResourceNotFound("NOT_FOUND", "Enter VALID Invoice Number or User Number")));
        Mono<UUID> userId = usersRepo.getUserIdByUserNo(paymentDTO.customerNo())
                .switchIfEmpty(Mono.error(new ResourceNotFound("NOT_FOUND", "Enter VALID Invoice Number or User Number")));
        return Mono.zip(invoiceId, userId)
                .flatMap(tuple -> {

                    UUID invoiceNumber = tuple.getT1();
                    UUID userNumber = tuple.getT2();

                   return invoiceRepo.invoiceExistsByUserId(userNumber)
                   .flatMap(
                            isInvCust-> {
                                if (!isInvCust){
                                    throw new ResourceNotFound("NOT_FOUND", "Enter VALID Invoice Number or User Number");
                                }
                                return Mono.just(Tuples.of(invoiceNumber, userNumber));
                            }
                    )
                           .flatMap(
                                   invCusTuples -> {
                                       PaymentEntity paymentEntity = new PaymentEntity();
                                       paymentEntity.setInvoiceId(invCusTuples.getT1());
                                       paymentEntity.setCustomerId(invCusTuples.getT2());
                                       paymentEntity.setAmount(paymentDTO.amount());
                                       paymentEntity.setNotes(paymentDTO.notes());
                                       paymentEntity.setPaymentMethod(paymentDTO.payment_method());
                                       paymentEntity.setTransactionRef(paymentDTO.transaction_ref());
                                       return paymentsRepo.save(paymentEntity)
                                               .flatMap(payment -> paymentsRepo.findById(payment.getId()))
                                               .map(pyEnt ->paymentMapper.topaymentResDTO(pyEnt,paymentDTO.customerNo(),paymentDTO.invoiceNo()));

                                   }
                           );

                })
                .doOnError(response -> Mono.error(() -> new RuntimeException(response.getMessage())));

    }

}
