package com.gkev.InvoicingSystem.Service;

import com.gkev.InvoicingSystem.models.DTO.EmailMessage;
import reactor.core.publisher.Mono;

public interface EmailServiceSender {

    Mono<Void> sendEmail(EmailMessage message);

    Mono<Void> sendEmailToOwners(EmailMessage template);}

