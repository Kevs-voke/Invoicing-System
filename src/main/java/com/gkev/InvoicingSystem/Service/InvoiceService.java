package com.gkev.InvoicingSystem.Service;

import com.gkev.InvoicingSystem.Exceptions.InvoiceCreationException;
import com.gkev.InvoicingSystem.Exceptions.ResourceNotFound;
import com.gkev.InvoicingSystem.models.DTO.*;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
@RequiredArgsConstructor
public class InvoiceService {
private final InvoiceItemsRepo invoiceItemsRepo;
private final InvoiceRepo invoiceRepo;
private final UsersRepo usersRepo;
private final Logger logger = LoggerFactory.getLogger(InvoiceService.class);
private final InvoicesCusRepo invoicesCusRepo;


    public Mono<InvoiceRespDTO> createInvoice(InvoiceDTO invoiceDTO) {
        logger.info("createInvoice for customer: {}", invoiceDTO.customerNo());

        Flux<InvoiceItemDTO> invoiceItems = Flux.fromIterable(invoiceDTO.items());
        return invoiceItems
                .flatMap(dto -> {
                    BigDecimal quantity = BigDecimal.valueOf(dto.quantity());
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
                                            return entity;
                                        })
                                        .toList();

                                return invoiceItemsRepo.saveAll(invItems)
                                        .collectList()
                                        .doOnSuccess(saved -> {
                                            if (saved != null) {
                                                logger.info("Saved {} invoice items for invoiceId={}",
                                                        saved.size(), invoice.getId());
                                            }
                                        })
                                        .doOnError(e -> logger.error("Failed to save invoice items for invoiceId={}: {}",
                                                invoice.getId(), e.getMessage()))
                                        .map(savedItems -> new InvoiceMapper().toInvoiceDTO(invoice, items, invoiceDTO.customerNo()));
                            });
                });
    }
    public Flux<InvoiceCustResDTO> getInvoices(InvoicesFilterDTO filter, int page, int size) {
        logger.info("Query for invoices with filters has started");
        return invoicesCusRepo.getInvoices(filter, page, size)
                .switchIfEmpty(Mono.error(() -> new ResourceNotFound("NOT_FOUND", " Invoices records could not be found")))
                .doOnComplete(() -> logger.info("Invoices records found  "));
    }
}
