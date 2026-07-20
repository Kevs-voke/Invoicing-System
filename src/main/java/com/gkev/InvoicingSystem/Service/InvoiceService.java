    package com.gkev.InvoicingSystem.Service;

    import com.gkev.InvoicingSystem.Exceptions.InvalidTransitionException;
    import com.gkev.InvoicingSystem.Exceptions.InvoiceCreationException;
    import com.gkev.InvoicingSystem.Exceptions.ResourceNotFound;
    import com.gkev.InvoicingSystem.models.DTO.*;
    import com.gkev.InvoicingSystem.models.Enums.Channel;
    import com.gkev.InvoicingSystem.models.Mapper.InvoiceMapper;
    import com.gkev.InvoicingSystem.models.entity.InvoiceItemsEntity;
    import com.gkev.InvoicingSystem.models.entity.InvoicesEntity;
    import com.gkev.InvoicingSystem.models.repo.InvoiceItemsRepo;
    import com.gkev.InvoicingSystem.models.repo.InvoiceRepo;
    import com.gkev.InvoicingSystem.models.repo.InvoicesCusRepo;
    import com.gkev.InvoicingSystem.models.repo.UsersRepo;
    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Service;
    import reactor.core.publisher.Flux;
    import reactor.core.publisher.Mono;

    import java.math.BigDecimal;
    import java.sql.Timestamp;
    import java.util.List;
    import java.util.Map;
    import java.util.Set;
    import java.util.UUID;

    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import reactor.core.scheduler.Schedulers;
    import reactor.util.function.Tuples;
    import tools.jackson.core.type.TypeReference;
    import tools.jackson.databind.ObjectMapper;

    import tools.jackson.databind.json.JsonMapper;
    import tools.jackson.databind.PropertyNamingStrategies;


    @Service
    @RequiredArgsConstructor
    public class InvoiceService {
    private final InvoiceItemsRepo invoiceItemsRepo;
    private final InvoiceRepo invoiceRepo;
    private final UsersRepo usersRepo;
    private final Logger logger = LoggerFactory.getLogger(InvoiceService.class);
    private final InvoicesCusRepo invoicesCusRepo;
    private final ObjectMapper objectMapper = JsonMapper.builder()
            .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
            .build();
    private final InvoiceConfirmationNotification invoiceConfirmationNotification;



    public Mono<InvoiceRespDTO> createInvoice(InvoiceDTO invoiceDTO) {
        logger.info("createInvoice for customer: {}", invoiceDTO.customerNo());

        Flux<InvoiceItemDTO> invoiceItems = Flux.fromIterable(invoiceDTO.items());
        return invoiceItems
                .flatMap(dto -> {
                    BigDecimal quantity = dto.quantity();
                    BigDecimal subTotal = dto.unitPrice().multiply(quantity);
                    BigDecimal taxSubtotal = dto.tax().multiply(quantity);
                    BigDecimal lineTotal = subTotal.add(taxSubtotal);

                    logger.debug("Item [{}]: qty={}, unitPrice={}, subTotal={}, taxSubtotal={}, lineTotal={}",
                            dto.itemName(), quantity, dto.unitPrice(), subTotal, taxSubtotal, lineTotal);

                    InvoiceItemsResDTO invoiceItem = new InvoiceItemsResDTO(
                            dto.itemName(),
                            dto.unitPrice(),
                            dto.quantity(),
                            dto.tax(),
                            taxSubtotal,
                            lineTotal
                    );
                    return Flux.just(invoiceItem);
                })
                .collectList()
                .flatMap(items -> {
                    BigDecimal subTotal = items.stream()
                            .map(InvoiceItemsResDTO::total)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal totalTax = items.stream()
                            .map(InvoiceItemsResDTO::tax_total)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    logger.info("Invoice totals — subTotal={}, totalTax={}, itemCount={}",
                            subTotal, totalTax, items.size());

                    return usersRepo.getUserIdByUserNo(invoiceDTO.customerNo())
                            .doOnSuccess(userId -> logger.info("Resolved customerId={} for customerNo={}",
                                    userId, invoiceDTO.customerNo()))
                            .doOnError(e -> logger.error("Failed to resolve customerId for customerNo={}: {}",
                                    invoiceDTO.customerNo(), e.getMessage()))
                            .flatMap(userId -> {
                                InvoicesEntity invoicesEntity = new InvoicesEntity();
                                invoicesEntity.setDueDate(Timestamp.valueOf(invoiceDTO.dueDate().atStartOfDay()));
                                invoicesEntity.setTotalTax(totalTax);
                                invoicesEntity.setTotal(subTotal);
                                invoicesEntity.setCustomerId(userId);
                                return invoiceRepo.save(invoicesEntity);
                            })
                            .doOnSuccess(inv -> {
                                if (inv != null) {
                                    logger.info("Invoice saved — invoiceId={}", inv.getId());
                                }
                            })
                            .onErrorMap(e -> new InvoiceCreationException(
                                    "INVOICE_SAVE_FAILED",
                                    "Failed to create invoice for customer: " + invoiceDTO.customerNo(),
                                    e))
                            .flatMap(invoice -> {
                                List<InvoiceItemsEntity> invItems = items.stream()
                                        .map(invoiceItem -> {
                                            InvoiceItemsEntity entity = new InvoiceItemsEntity();
                                            entity.setInvoiceId(invoice.getId());
                                            entity.setItemName(invoiceItem.itemName());
                                            entity.setUnitPrice(invoiceItem.unitPrice());
                                            entity.setSubTotal(invoiceItem.total());
                                            entity.setTax(invoiceItem.tax());
                                            entity.setTaxSubtotal(invoiceItem.tax_total());
                                            entity.setQuantity(invoiceItem.quantity());
                                            return entity;
                                        })
                                        .toList();
                                        return invoiceItemsRepo.saveAll(invItems)
        .collectList()
        .doOnSuccess(saved -> {
            if (saved != null) {
                logger.info("Saved {} invoice items for invoiceId={}", saved.size(), invoice.getId());
            }
        })
        .doOnError(e -> logger.error("Failed to save invoice items for invoiceId={}: {}", 
                invoice.getId(), e.getMessage()))
        .flatMap(savedItems -> 
            // Re-fetch the full invoice with generated fields
            invoiceRepo.findById(invoice.getId())
                .map(fullInvoice -> new InvoiceMapper().toInvoiceDTO(fullInvoice, items, invoiceDTO.customerNo()))
                .switchIfEmpty(Mono.error(new RuntimeException("Invoice not found after save: " + invoice.getId())))
        );
                            
                    });                 
                });
    }
   public Flux<InvoiceCustResDTO> getInvoices(InvoicesFilterDTO filter, int page, int size) {
    logger.info("Query for invoices with filters has started");
    return invoicesCusRepo.getInvoices(filter, page, size)
            .doOnComplete(() -> logger.info("Invoices query completed"));
    }
    public Mono<InvoiceDashboardStatsDTO> getInvoiceDashboardStats() {
        logger.info("Query for invoices dashboard stats has started");
        return invoiceRepo.getInvoiceDashboardStats()
                .switchIfEmpty(Mono.error(() -> new ResourceNotFound("NOT_FOUND", " invoices dashboard stats could not be found")))
                .doOnSuccess(response ->logger.info("Invoices dashboard stats found"));
    }

    public Mono<DetailedInvoiceResDTO> getDetailedInvoice(long invoiceNo, long customerNo) {

        logger.info("Querying detailed invoice for invoice: {} has started", invoiceNo);
        logger.info("Validation of invoice and customer has started");
        Mono<UUID> invoiceId = invoiceRepo.getInvoiceIdByInvoiceNo(invoiceNo)
                .switchIfEmpty(Mono.error(new ResourceNotFound("NOT_FOUND", "Enter VALID Invoice Number or User Number")));
        Mono<UUID> userId = usersRepo.getUserIdByUserNo(customerNo)
                .switchIfEmpty(Mono.error(new ResourceNotFound("NOT_FOUND", "Enter VALID Invoice Number or User Number")));
        return Mono.zip(invoiceId, userId)
                .flatMap(tuple -> {

                    UUID invoiceNumber = tuple.getT1();
                    UUID userNumber = tuple.getT2();

                    return invoiceRepo.invoiceExistsForCustomer(invoiceNumber, userNumber)
                            .flatMap(
                                    isInvCust -> {
                                        if (!isInvCust) {
                                            throw new ResourceNotFound("NOT_FOUND", "Enter VALID Invoice Number or User Number");
                                        }
                                        logger.info("Valid customer Number and Invoice Number");
                                        return Mono.just(Tuples.of(invoiceNumber, userNumber));
                                    }
                            )
                            .flatMap(
                                    invCusTuple -> invoiceRepo.getDetailedInvoiceById(invoiceNumber)
                                            .switchIfEmpty(Mono.error(() -> new ResourceNotFound("NOT_FOUND", "Enter VALID Invoice Number or User Number")))
                                            .flatMap(invoice -> Mono.fromCallable(() -> {
                                                        List<InvoiceItemsResDTO> invoiceItems = parseInvoiceItems(invoice.invoiceItems());
                                                        return new DetailedInvoiceResDTO(
                                                                invoice.invoiceNo(),
                                                                invoice.status(),
                                                                invoice.dueDate(),
                                                                invoice.totalTax(),
                                                                invoice.total(),
                                                                invoice.amountPaid(),
                                                                invoice.balances(),
                                                                invoiceItems
                                                        );
                                                    })
                                                    .subscribeOn(Schedulers.parallel())));

                });
    }

    private List<InvoiceItemsResDTO> parseInvoiceItems(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            logger.error("Failed to parse json response", e);
            throw new RuntimeException("Failed to parse Invoice Items JSON", e);
        }
    }
private List<TopCustomerRecords> parseTopCustomerRecords(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        }catch (Exception e) {
            throw new RuntimeException("Failed to parse Top Customer Records JSON", e);
        }
}
    public Flux<OverdueInvoiceDTO> getOverdueInvoiceCust() {
        return invoiceRepo.getOverdueInvoiceCust();
    }

    public Mono<InvoiceSummaryReport> getInvoiceSummaryReport() {
        return invoiceRepo.getInvoiceMonthlySummaryReport()
                .switchIfEmpty(Mono.error(()-> new ResourceNotFound("NOT_FOUND", "Monthly Summary Report could not found")))
                .flatMap(report -> Mono.fromCallable(
                        () -> {
                            if (report.topCustomerRecords() == null) {
                                logger.warn("Top customer records are null in the report for period: {}", report.reportPeriod());
                            }
                            logger.info("{}", report.topCustomerRecords());
                            List<TopCustomerRecords>  topCustomerRecords =parseTopCustomerRecords(report.topCustomerRecords());
                            return new InvoiceSummaryReport(
                                    report.reportPeriod(),
                                    report.generatedOn(),
                                    report.totalRevenue(),
                                    report.outstandingTotal(),
                                    report.currentTotal(),
                                    report.currentCount(),
                                    report.overdueCount(),
                                    report.overdueTotal(),
                                    report.outstandingCount(),
                                    topCustomerRecords
                            );
                        }
                ));
    }

    public Mono<Void> updateStatus(long invoiceNo, String currentStatus) {
        logger.info("Updating Invoice Status for Invoice No: {}", invoiceNo);
        return invoiceRepo.getInvoiceStatus(invoiceNo)
                .flatMap(prevStatus -> {
                    if( !isValidStatusTransition(prevStatus, currentStatus)){
                        throw new InvalidTransitionException("ILLEGAL_INVOICE_STATUS_TRANSITION","This invoice cannot be updated to the requested status.");
                    }
                  return   invoiceRepo.updateInvoiceStatus(invoiceNo)
                          .doOnSuccess(r -> logger.info("Successfully updated Invoice Status for Invoice No: {}", invoiceNo));
                });
    }

        private static  Map<String, Set<String>> invoiceStatusTransitions() {
           return Map.of(
                    "draft", Set.of("sent", "cancelled"),
                    "sent", Set.of("rejected", "cancelled", "pending"),
                    "pending", Set.of("cancelled", "paid", "overdue"),
                    "paid", Set.of(),
                    "overdue", Set.of("paid"),
                    "cancelled", Set.of(),
                    "rejected", Set.of("sent","cancelled")
            );
        }
        public boolean isValidStatusTransition(String from, String to) {
            Set<String> allowed = invoiceStatusTransitions().get(from);
            return allowed != null && allowed.contains(to);
        }
       private Mono<InvoiceConfirmationResDTO> getInvoiceConfirmationDetails(long invoiceNo) {
        return invoiceRepo.getConfirmInvoice(invoiceNo)
                .flatMap(invoice -> {
                    List<InvoiceItemsResDTO> items = parseInvoiceItems(invoice.invoiceItems());
                    return  Mono.just( new InvoiceConfirmationResDTO(
                            invoice.customerName(),
                            invoice.email(),
                            invoice.invoiceNo(),
                            invoice.totalTax(),
                            invoice.total(),
                            invoice.dueDate(),
                            items
                    ));
                });

       }
       public Mono<Void> notifyCustomerInvoiceCreated(long invoiceNo) {
        return updateStatus(invoiceNo, "sent")
                .flatMap(x -> getInvoiceConfirmationDetails(invoiceNo))
                .flatMap(invoice -> invoiceConfirmationNotification.send(Channel.EMAIL, invoice))
                .doOnSuccess(res -> logger.info("customer notified Successfully"));
       }
    }
