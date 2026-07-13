package com.gkev.InvoicingSystem.models.DTO;

public record EmailMessage(
        String to,
        String subject,
        String htmlBody,
        byte[] attachment,
        String attachmentFilename
) {
    public boolean hasAttachment() {
        return attachment != null && attachment.length > 0;
    }
}