package com.gkev.InvoicingSystem.Service;

import com.gkev.InvoicingSystem.Exceptions.PdfGenerationException;
import com.itextpdf.html2pdf.HtmlConverter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.ByteArrayOutputStream;
@Service
public class PdfGeneratorService {

    public Mono<byte[]> htmlToPdf(String html) {
        return Mono.fromCallable(() -> {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    HtmlConverter.convertToPdf(html, out);
                    return out.toByteArray();
                })
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorMap(ex ->
                        new PdfGenerationException(
                                "PDF_GENERATION_FAILED",
                                "Failed to generate PDF.",
                                ex
                        )
                );
    }
}
