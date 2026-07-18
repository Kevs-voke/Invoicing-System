package com.gkev.InvoicingSystem.Service;

import com.gkev.InvoicingSystem.Exceptions.EmailSendException;
import com.gkev.InvoicingSystem.models.DTO.EmailMessage;
import com.gkev.InvoicingSystem.models.repo.UsersRepo;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class EmailService implements EmailServiceSender {

    private final JavaMailSender javaMailSender;
    private final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Override
    public Mono<Void> sendEmail(EmailMessage message) {
        return Mono.fromRunnable(() -> {
                    try {

                        if (message == null) {
                            throw new IllegalArgumentException("EmailMessage cannot be null");
                        }

                        String toAddress = message.to();
                        if (toAddress == null || toAddress.trim().isEmpty()) {
                            throw new IllegalArgumentException(
                                    "Email 'To' address must not be null or empty. " +
                                            "Check your MonthlySummaryJob and report configuration.");
                        }

                        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
                        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

                        helper.setTo(toAddress);
                        helper.setSubject(message.subject());
                        helper.setText(message.htmlBody(), true);

                        if (message.attachment() != null && message.attachmentFilename() != null) {
                            helper.addAttachment(
                                    message.attachmentFilename(),
                                    new ByteArrayResource(message.attachment()));
                        }

                        javaMailSender.send(mimeMessage);

                        logger.info("Email sent successfully to: {}", toAddress);

                    } catch (MessagingException e) {
                        throw new EmailSendException("EMAIL_SEND_FAILED",
                                "Failed to send email to " + message.to(), e);
                    }
                })
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }



}