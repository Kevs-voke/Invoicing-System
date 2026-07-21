package com.gkev.InvoicingSystem.models.DTO;

public record EmailMessage(
        String to,
        String subject,
        String htmlBody,
        byte[] attachment,
        String attachmentFilename
) {
    public EmailMessage(String to, String subject, String htmlBody) {
        this(to, subject, htmlBody, null, null);
    }

    public boolean hasAttachment() {
        return attachment != null && attachment.length > 0;
    }
}