package com.gkev.InvoicingSystem.Service;

import com.gkev.InvoicingSystem.Exceptions.EmailSendException;
import com.gkev.InvoicingSystem.models.DTO.EmailMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService implements EmailServiceSender {

    private final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Value("${spring.mail.username}")
    private String fromEmail;
    @Value("${spring.mail.from}")
    private String fromName;
    @Value("${app.brevo.api-key}")
    private String brevoApiKey;

    private final WebClient brevoWebClient = WebClient.builder()
            .baseUrl("https://api.brevo.com/v3")
            .build();

    @Override
    public Mono<Void> sendEmail(EmailMessage message) {
        if (message == null) {
            return Mono.error(new IllegalArgumentException("EmailMessage cannot be null"));
        }
        String toAddress = message.to();
        if (toAddress == null || toAddress.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException(
                    "Email 'To' address must not be null or empty. " +
                            "Check your MonthlySummaryJob and report configuration."));
        }

        Map<String, Object> body = new java.util.HashMap<>();
        body.put("sender", Map.of("name", fromName, "email", fromEmail));
        body.put("to", List.of(Map.of("email", toAddress)));
        body.put("subject", message.subject());
        body.put("htmlContent", message.htmlBody());

        if (message.hasAttachment()) {
            String base64Content = Base64.getEncoder().encodeToString(message.attachment());
            body.put("attachment", List.of(Map.of(
                    "content", base64Content,
                    "name", message.attachmentFilename()
            )));
        }

        return brevoWebClient.post()
                .uri("/smtp/email")
                .header("api-key", brevoApiKey)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .bodyValue(body)
                .retrieve()
                .toBodilessEntity()
                .doOnSuccess(response -> logger.info("Email sent successfully to: {}", toAddress))
                .onErrorMap(e -> new EmailSendException(
                        "EMAIL_SEND_FAILED",
                        "Failed to send email to " + toAddress,
                        e))
                .then();
    }
}