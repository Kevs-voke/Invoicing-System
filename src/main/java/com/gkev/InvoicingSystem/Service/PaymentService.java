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
import java.math.BigDecimal;

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
    private final InvoiceService invoiceService;
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

               return invoiceRepo.invoiceExistsForCustomer(invoiceNumber, userNumber)
               .flatMap(
                        isInvCust-> {
                            if (!isInvCust){
                                throw new ResourceNotFound("NOT_FOUND", "Enter VALID Invoice Number or User Number");
                            }
                            return Mono.just(Tuples.of(invoiceNumber, userNumber));
                        }
                )
                       .flatMap(
                               invCusTuples -> invoiceRepo.getOutstandingBalance(invCusTuples.getT1())
                                       .flatMap(outstanding -> {
                                           if (paymentDTO.amount().compareTo(outstanding) > 0) {
                                               return Mono.error(new UserException(
                                                       "PAYMENT_EXCEEDS_BALANCE",
                                                       "This payment (%s) exceeds the invoice's outstanding balance (%s)."
                                                               .formatted(paymentDTO.amount(), outstanding)
                                               ));
                                           }

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
                                                   .map(pyEnt -> paymentMapper.topaymentResDTO(pyEnt, paymentDTO.customerNo(), paymentDTO.invoiceNo()));
                                       })
                       );

            })
            .doOnError(response -> Mono.error(() -> new RuntimeException(response.getMessage())));

}

    public Mono<PaymentResDTO> makeMyPayment(PaymentDTO paymentDTO) {
        return currentUserNo()
                .flatMap(customerNo -> makePayment(new PaymentDTO(
                        customerNo,
                        paymentDTO.invoiceNo(),
                        paymentDTO.amount(),
                        paymentDTO.transaction_ref(),
                        paymentDTO.payment_method(),
                        paymentDTO.notes()
                )));
    }

    public Flux<PaymentCustResDTO> getPayments(PaymentsFilterDTO filter, int page, int size) {
    logger.info("Query for payments with filters has started");
    return paymentsCusRepo.getPayments(filter, page, size)
        .doOnComplete(() -> logger.info("Payments records found"));
}

    public Flux<PaymentCustResDTO> getMyPayments(PaymentsFilterDTO filter, int page, int size) {
        return currentUserNo()
                .flatMapMany(customerNo -> getPayments(forCustomer(filter, customerNo), page, size));
    }

    private Mono<UUID> currentUserId() {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ((UserPrincipal) ctx.getAuthentication().getPrincipal()).getUserId());
    }

    private Mono<Long> currentUserNo() {
        return currentUserId().flatMap(usersRepo::getUserNoByUserId);
    }

    private PaymentsFilterDTO forCustomer(PaymentsFilterDTO filter, Long customerNo) {
        return new PaymentsFilterDTO(
                customerNo,
                filter.invoiceNo(),
                filter.paymentNo(),
                null,
                null,
                filter.paymentMethod(),
                filter.status(),
                filter.paymentDateFrom(),
                filter.paymentDateTo(),
                filter.sortBy(),
                filter.sortDirection()
        );
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
                return invoiceRepo.getOutstandingBalance(pymt.getInvoiceId())
                        .flatMap(outstanding -> {
                            if (pymt.getAmount().compareTo(outstanding) > 0) {
                                return Mono.error(new UserException(
                                        "PAYMENT_EXCEEDS_BALANCE",
                                        "This payment (%s) exceeds the invoice's outstanding balance (%s)."
                                                .formatted(pymt.getAmount(), outstanding)
                                ));
                            }
                            pymt.setStatus("confirmed");
                            pymt.setConfirmedAt(Timestamp.valueOf(LocalDateTime.now()));
                            pymt.setConfirmedBy(managerId);

                            BigDecimal remainingAfterThisPayment = outstanding.subtract(pymt.getAmount());

                            return paymentsRepo.save(pymt)
                                    .flatMap(saved -> invoiceRepo.incrementAmountPaid(saved.getInvoiceId(), saved.getAmount())
                                            .then(advanceInvoiceStatusAfterPayment(saved.getInvoiceId(), remainingAfterThisPayment))
                                            .thenReturn(saved));
                        });
            })
            .flatMap(this::toPaymentResDTOWithFriendlyNumbers);
}

private Mono<Void> advanceInvoiceStatusAfterPayment(UUID invoiceId, BigDecimal remainingBalance) {
    return Mono.zip(invoiceRepo.getInvoiceStatusById(invoiceId), invoiceRepo.getInvoiceNoByInvoiceId(invoiceId))
            .flatMap(tuple -> {
                String currentStatus = tuple.getT1();
                Long invoiceNo = tuple.getT2();

                Mono<Void> advanceToPending = "sent".equalsIgnoreCase(currentStatus)
                        ? invoiceService.updateStatus(invoiceNo, "pending")
                        : Mono.empty();

                return advanceToPending.then(
                        remainingBalance.compareTo(BigDecimal.ZERO) <= 0
                                ? invoiceService.updateStatus(invoiceNo, "paid")
                                : Mono.empty()
                );
            });
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

    public Mono<DetailedPaymentResDTO> getMyDetailedPayment(Long paymentNo) {
        return Mono.zip(getDetailedPayment(paymentNo), currentUserNo())
                .flatMap(tuple -> {
                    DetailedPaymentResDTO payment = tuple.getT1();
                    Long customerNo = tuple.getT2();
                    if (!customerNo.equals(payment.customerNo())) {
                        return Mono.error(new ResourceNotFound("NOT_FOUND", "Payment not found"));
                    }
                    return Mono.just(payment);
                });
    }

}
