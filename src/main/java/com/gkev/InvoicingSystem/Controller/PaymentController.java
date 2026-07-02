package com.gkev.InvoicingSystem.Controller;

import com.gkev.InvoicingSystem.Service.PaymentService;
import com.gkev.InvoicingSystem.models.DTO.PaymentDTO;
import com.gkev.InvoicingSystem.models.DTO.PaymentResDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    @PostMapping
    public Mono<ResponseEntity<PaymentResDTO>> makePayment(PaymentDTO paymentDTO) {
        return paymentService.makePayment(paymentDTO)
                .map(payment -> ResponseEntity.status(HttpStatus.CREATED).body(payment));
    }
}
