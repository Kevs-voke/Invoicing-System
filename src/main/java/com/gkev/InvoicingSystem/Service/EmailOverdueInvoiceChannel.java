package com.gkev.InvoicingSystem.Service;

import com.gkev.InvoicingSystem.models.DTO.EmailMessage;
import com.gkev.InvoicingSystem.models.DTO.OverdueInvoiceDTO;
import com.gkev.InvoicingSystem.models.Enums.Channel;
import com.gkev.InvoicingSystem.models.Mapper.DailyOverdueMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class EmailOverdueInvoiceChannel implements DailyOverdueChannel {

    private final SpringTemplateEngine emailTemplateEngine;
    private final EmailService emailService;
    private final DailyOverdueMapper mapper;
    private final PdfGeneratorService pdfGenerator;
    private final Logger logger = LoggerFactory.getLogger(EmailOverdueInvoiceChannel.class);

    @Override

    public Mono<Void> send(OverdueInvoiceDTO invoice) {
        logger.info("Started sending overdue email");
        return mapper.setData(invoice)
                .flatMap(overdueInvoice ->{
                    String html =emailTemplateEngine.process("DailyOverdueEmail",overdueInvoice);
                    return pdfGenerator.htmlToPdf(html)
                            .map(pdf -> new EmailMessage(
                                    invoice.email(),
                                    "",
                                    html
                            ))
                            .flatMap(emailService::sendEmail)
                            .doOnSuccess(x-> logger.info("completed sending overdue email"));

                        }
                );
    }

    @Override
    public Channel channel() {
        return Channel.EMAIL;
    }

}
