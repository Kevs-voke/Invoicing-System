package com.gkev.InvoicingSystem.Service;

import com.gkev.InvoicingSystem.models.DTO.EmailMessage;
import com.gkev.InvoicingSystem.models.DTO.InvoiceConfirmationResDTO;
import com.gkev.InvoicingSystem.models.Enums.Channel;
import com.gkev.InvoicingSystem.models.Mapper.ConfirmationInvoiceEmailMapper;
import com.gkev.InvoicingSystem.models.Mapper.ConfirmationInvoiceMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class EmailInvoiceConfirmationChannel implements  InvoiceConfirmationChannel {


    private final PdfGeneratorService pdfGenerator;
    private final ConfirmationInvoiceMapper  confirmationInvoiceMapper;
    private final ConfirmationInvoiceEmailMapper emailMapper;
    private final SpringTemplateEngine emailTemplateEngine;
    private final EmailService emailService;
    private final Logger logger = LoggerFactory.getLogger(EmailInvoiceConfirmationChannel.class);

    @Override
    public Mono<Void> send(InvoiceConfirmationResDTO invoice) {
        return confirmationInvoiceMapper.setData(invoice)
                .flatMap(invoiceContext -> {
                    String invoiceHtml = emailTemplateEngine.process("InvoiceConfirmationPdf",invoiceContext);
                    return pdfGenerator.htmlToPdf(invoiceHtml)
                            .flatMap(pdf -> emailMapper.setData(invoice)
                                    .map(confInvEmail -> {
                                        String html = emailTemplateEngine.process("EmailConfirmation",confInvEmail);
                                        return new EmailMessage(
                                            invoice.email(),
                                            "Thank You for Your Order – Your Invoice Is Attached",
                                                html,
                                                pdf,
                                                invoice.invoiceNo()+".pdf"

                                        );
                                    }));


                })
                .flatMap(emailTemplate -> emailService.sendEmail(emailTemplate)
                        .doOnSuccess(unused -> logger.info("Monthly summary report email sent successfully"))
                        );

    }

    @Override
    public Channel channel() {
        return Channel.EMAIL;
    }
}
