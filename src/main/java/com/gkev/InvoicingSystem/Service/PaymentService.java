package com.gkev.InvoicingSystem.Service;

import com.gkev.InvoicingSystem.Exceptions.ResourceNotFound;
import com.gkev.InvoicingSystem.models.DTO.PaymentDTO;
import com.gkev.InvoicingSystem.models.DTO.PaymentResDTO;
import com.gkev.InvoicingSystem.models.Mapper.PaymentMapper;
import com.gkev.InvoicingSystem.models.UserPrincipal;
import com.gkev.InvoicingSystem.models.entity.PaymentEntity;
import com.gkev.InvoicingSystem.models.repo.InvoiceRepo;
import com.gkev.InvoicingSystem.models.repo.PaymentsRepo;
import com.gkev.InvoicingSystem.models.DTO.DetailedPaymentResDTO;
import com.gkev.InvoicingSystem.models.repo.UsersRepo;
import com.gkev.InvoicingSystem.models.DTO.PaymentDashboardStatsDTO;
import com.gkev.InvoicingSystem.Exceptions.UserException;

import com.gkev.InvoicingSystem.models.DTO.PaymentCustResDTO;
import com.gkev.InvoicingSystem.models.DTO.PaymentsFilterDTO;
import com.gkev.InvoicingSystem.models.repo.PaymentsCusRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import reactor.core.publisher.Flux;
import java.sql.Timestamp;
import java.time.LocalDateTime;

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
    private final PaymentsCusRepo paymentsCusRepo;
    private final Logger logger = LoggerFactory.getLogger(PaymentService.class);


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
                                       paymentEntity.setStatus("pending");
                                       paymentEntity.setTransactionRef(paymentDTO.transaction_ref());
                                       paymentEntity.setPaymentAt(Timestamp.valueOf(LocalDateTime.now()));
                                       paymentEntity.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));

                                       return paymentsRepo.save(paymentEntity)
                                               .flatMap(payment -> paymentsRepo.findById(payment.getId()))
                                               .map(pyEnt ->paymentMapper.topaymentResDTO(pyEnt,paymentDTO.customerNo(),paymentDTO.invoiceNo()));

                                   }
                           );

                })
                .doOnError(response -> Mono.error(() -> new RuntimeException(response.getMessage())));

    }
    public Flux<PaymentCustResDTO> getPayments(PaymentsFilterDTO filter, int page, int size) {
        logger.info("Query for payments with filters has started");
        return paymentsCusRepo.getPayments(filter, page, size)
        .switchIfEmpty(Mono.error(new ResourceNotFound("NOT_FOUND", "Payment records could not be found")))
        .doOnComplete(() -> logger.info("Payments records found"));
    }

    private Mono<UUID> currentUserId() {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ((UserPrincipal) ctx.getAuthentication().getPrincipal()).getUserId());
    }

    public Mono<PaymentResDTO> confirmPayment(Long paymentNo) {
        Mono<PaymentEntity> payment = paymentsRepo.getPaymentIdByPaymentNo(paymentNo)
                .switchIfEmpty(Mono.error(new ResourceNotFound("NOT_FOUND", "Payment not found")))
                .flatMap(paymentsRepo::findById)
                .switchIfEmpty(Mono.error(new ResourceNotFound("NOT_FOUND", "Payment not found")));

        return Mono.zip(payment, currentUserId())
                .flatMap(tuple -> {
                    PaymentEntity pymt = tuple.getT1();
                    UUID managerId = tuple.getT2();
                    if (!"pending".equalsIgnoreCase(pymt.getStatus())) {
                        return Mono.error(new UserException("INVALID_STATE", "Only pending payments can be confirmed"));
                    }
                    pymt.setStatus("confirmed");
                    pymt.setConfirmedAt(Timestamp.valueOf(LocalDateTime.now()));
                    pymt.setConfirmedBy(managerId);
                    return paymentsRepo.save(pymt)
                            .flatMap(saved -> invoiceRepo.incrementAmountPaid(saved.getInvoiceId(), saved.getAmount())
                                    .thenReturn(saved));
                })
                .flatMap(this::toPaymentResDTOWithFriendlyNumbers);
    }

    public Mono<PaymentResDTO> failPayment(Long paymentNo) {
        Mono<PaymentEntity> payment = paymentsRepo.getPaymentIdByPaymentNo(paymentNo)
                .switchIfEmpty(Mono.error(new ResourceNotFound("NOT_FOUND", "Payment not found")))
                .flatMap(paymentsRepo::findById)
                .switchIfEmpty(Mono.error(new ResourceNotFound("NOT_FOUND", "Payment not found")));

        return Mono.zip(payment, currentUserId())
                .flatMap(tuple -> {
                    PaymentEntity pymt = tuple.getT1();
                    UUID managerId = tuple.getT2();
                    if (!"pending".equalsIgnoreCase(pymt.getStatus())) {
                        return Mono.error(new UserException("INVALID_STATE", "Only pending payments can be failed"));
                    }
                    pymt.setStatus("failed");
                    pymt.setFailedAt(Timestamp.valueOf(LocalDateTime.now()));
                    pymt.setFailedBy(managerId);
                    return paymentsRepo.save(pymt);
                })
                .flatMap(this::toPaymentResDTOWithFriendlyNumbers);
    }

    public Mono<PaymentDashboardStatsDTO> getPaymentDashboardStats() {
        return paymentsRepo.getPaymentDashboardStats();
    }

    private Mono<PaymentResDTO> toPaymentResDTOWithFriendlyNumbers(PaymentEntity payment) {
        Mono<Long> invoiceNo = invoiceRepo.getInvoiceNoByInvoiceId(payment.getInvoiceId());
        Mono<Long> customerNo = usersRepo.getUserNoByUserId(payment.getCustomerId());

        return Mono.zip(invoiceNo, customerNo)
                .map(tuple -> paymentMapper.topaymentResDTO(payment, tuple.getT2(), tuple.getT1()));
    }

    public Mono<DetailedPaymentResDTO> getDetailedPayment(Long paymentNo) {
        return paymentsRepo.getDetailedPaymentByPaymentNo(paymentNo)
            .switchIfEmpty(Mono.error(new ResourceNotFound("NOT_FOUND", "Payment not found")));
    }

}