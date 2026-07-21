package com.gkev.InvoicingSystem.Controller;

import com.gkev.InvoicingSystem.Service.PaymentService;
import com.gkev.InvoicingSystem.models.DTO.PaymentDTO;
import com.gkev.InvoicingSystem.models.DTO.PaymentResDTO;
import com.gkev.InvoicingSystem.models.DTO.DetailedPaymentResDTO;

import com.gkev.InvoicingSystem.models.DTO.PaymentCustResDTO;
import com.gkev.InvoicingSystem.models.DTO.PaymentsFilterDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.gkev.InvoicingSystem.models.DTO.PaymentDashboardStatsDTO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import jakarta.validation.Valid;
import java.util.UUID;

import java.util.List; 

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public Mono<ResponseEntity<PaymentResDTO>> makePayment(@Valid @RequestBody PaymentDTO paymentDTO) {
        return paymentService.makePayment(paymentDTO)
                .map(payment -> ResponseEntity.status(HttpStatus.CREATED).body(payment));
    }

    @PostMapping("/mine")
    @PreAuthorize("hasRole('CUSTOMER')")
    public Mono<ResponseEntity<PaymentResDTO>> makeMyPayment(@Valid @RequestBody PaymentDTO paymentDTO) {
        return paymentService.makeMyPayment(paymentDTO)
                .map(payment -> ResponseEntity.status(HttpStatus.CREATED).body(payment));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public Mono<ResponseEntity<List<PaymentCustResDTO>>> getPayments(
        PaymentsFilterDTO filter,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
            return paymentService.getPayments(filter, page, size)
                .collectList()
                .map(ResponseEntity::ok);
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public Mono<ResponseEntity<PaymentDashboardStatsDTO>> getPaymentDashboardStats() {
        return paymentService.getPaymentDashboardStats().map(ResponseEntity::ok);
    }

    @GetMapping("/detailed-payment")
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public Mono<ResponseEntity<DetailedPaymentResDTO>> getDetailedPayment(@RequestParam Long paymentNo){
        return paymentService.getDetailedPayment(paymentNo)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/mine")
    @PreAuthorize("hasRole('CUSTOMER')")
    public Mono<ResponseEntity<List<PaymentCustResDTO>>> getMyPayments(
            PaymentsFilterDTO filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return paymentService.getMyPayments(filter, page, size)
                .collectList()
                .map(ResponseEntity::ok);
    }

    @GetMapping("/mine/{paymentNo}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public Mono<ResponseEntity<DetailedPaymentResDTO>> getMyDetailedPayment(@PathVariable Long paymentNo) {
        return paymentService.getMyDetailedPayment(paymentNo)
                .map(ResponseEntity::ok);
    }
    @PatchMapping("/{paymentNo}/confirm")
    @PreAuthorize("hasRole('MANAGER')")
    public Mono<ResponseEntity<PaymentResDTO>> confirmPayment(@PathVariable Long paymentNo) {
        return paymentService.confirmPayment(paymentNo).map(ResponseEntity::ok);
    }

    @PatchMapping("/{paymentNo}/fail")
    @PreAuthorize("hasRole('MANAGER')")
    public Mono<ResponseEntity<PaymentResDTO>> failPayment(@PathVariable Long paymentNo) {
        return paymentService.failPayment(paymentNo).map(ResponseEntity::ok);
    }


}
