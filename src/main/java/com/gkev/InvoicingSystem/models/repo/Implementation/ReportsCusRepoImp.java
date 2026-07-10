package com.gkev.InvoicingSystem.models.repo.Implementation;

import com.gkev.InvoicingSystem.models.DTO.PaymentMethodBreakdownDTO;
import com.gkev.InvoicingSystem.models.DTO.ReportsFilterDTO;
import com.gkev.InvoicingSystem.models.DTO.ReportsSummaryDTO;
import com.gkev.InvoicingSystem.models.DTO.RevenuePointDTO;
import com.gkev.InvoicingSystem.models.DTO.TopCustomerDTO;
import com.gkev.InvoicingSystem.models.Enums.ReportGranularity;
import com.gkev.InvoicingSystem.models.repo.ReportsCusRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReportsCusRepoImp implements ReportsCusRepo {

    private final DatabaseClient client;

    @Override
    public Mono<ReportsSummaryDTO> getSummary(ReportsFilterDTO filter) {
        LocalDate from = filter.from();
        LocalDate toExclusive = filter.to().plusDays(1);
        long periodDays = ChronoUnit.DAYS.between(from, toExclusive);
        LocalDate priorFrom = from.minusDays(periodDays);
        LocalDate priorToExclusive = from;

        String sql = """
            SELECT
              COALESCE(SUM(CASE WHEN p.payment_at >= :from AND p.payment_at < :toExclusive THEN p.amount ELSE 0 END), 0) AS current_payments,
              COALESCE(SUM(CASE WHEN p.payment_at >= :priorFrom AND p.payment_at < :priorToExclusive THEN p.amount ELSE 0 END), 0) AS prior_payments
            FROM payments p
            WHERE p.status = 'confirmed'
              AND p.payment_at >= :priorFrom AND p.payment_at < :toExclusive
            """;

        String invoiceSql = """
            SELECT
              COUNT(CASE WHEN created_at >= :from AND created_at < :toExclusive THEN 1 END) AS current_invoices,
              COUNT(CASE WHEN created_at >= :priorFrom AND created_at < :priorToExclusive THEN 1 END) AS prior_invoices
            FROM invoice
            WHERE created_at >= :priorFrom AND created_at < :toExclusive
            """;

        String outstandingSql = """
            SELECT
              COALESCE(SUM(CASE WHEN created_at < :toExclusive THEN (total - amount_paid) ELSE 0 END), 0) AS current_outstanding,
              COALESCE(SUM(CASE WHEN created_at < :priorToExclusive THEN (total - amount_paid) ELSE 0 END), 0) AS prior_outstanding
            FROM invoice
            WHERE status <> 'PAID'
            """;

        Mono<BigDecimal[]> paymentsMono = client.sql(sql)
                .bind("from", from).bind("toExclusive", toExclusive)
                .bind("priorFrom", priorFrom).bind("priorToExclusive", priorToExclusive)
                .map((row, meta) -> new BigDecimal[]{
                        row.get("current_payments", BigDecimal.class),
                        row.get("prior_payments", BigDecimal.class)
                }).one();

        Mono<Long[]> invoicesMono = client.sql(invoiceSql)
                .bind("from", from).bind("toExclusive", toExclusive)
                .bind("priorFrom", priorFrom).bind("priorToExclusive", priorToExclusive)
                .map((row, meta) -> new Long[]{
                        row.get("current_invoices", Long.class),
                        row.get("prior_invoices", Long.class)
                }).one();

        Mono<BigDecimal[]> outstandingMono = client.sql(outstandingSql)
                .bind("toExclusive", toExclusive)
                .bind("priorToExclusive", priorToExclusive)
                .map((row, meta) -> new BigDecimal[]{
                        row.get("current_outstanding", BigDecimal.class),
                        row.get("prior_outstanding", BigDecimal.class)
                }).one();

        return Mono.zip(paymentsMono, invoicesMono, outstandingMono)
                .map(tuple -> {
                    BigDecimal[] payments = tuple.getT1();
                    Long[] invoices = tuple.getT2();
                    BigDecimal[] outstanding = tuple.getT3();

                    BigDecimal currentPayments = payments[0];
                    BigDecimal priorPayments = payments[1];
                    BigDecimal currentOutstanding = outstanding[0];
                    BigDecimal priorOutstanding = outstanding[1];

                    return ReportsSummaryDTO.builder()
                            .totalRevenue(currentPayments)
                            .revenueChangePct(pctChange(currentPayments, priorPayments))
                            .totalPayments(currentPayments)
                            .paymentsChangePct(pctChange(currentPayments, priorPayments))
                            .totalInvoices(invoices[0])
                            .invoicesChangePct(pctChange(BigDecimal.valueOf(invoices[0]), BigDecimal.valueOf(invoices[1])))
                            .outstandingAmount(currentOutstanding)
                            .outstandingChangePct(pctChange(currentOutstanding, priorOutstanding))
                            .build();
                });
    }

    @Override
    public Flux<RevenuePointDTO> getRevenueSeries(ReportsFilterDTO filter) {
        String truncUnit = switch (filter.granularity()) {
            case DAILY -> "day";
            case WEEKLY -> "week";
            case MONTHLY -> "month";
        };
        LocalDate toExclusive = filter.to().plusDays(1);

        String sql = """
            SELECT date_trunc('%s', p.payment_at)::date AS bucket_date, SUM(p.amount) AS amount
            FROM payments p
            WHERE p.status = 'confirmed'
              AND p.payment_at >= :from AND p.payment_at < :toExclusive
            GROUP BY bucket_date
            ORDER BY bucket_date
            """.formatted(truncUnit);

        return client.sql(sql)
                .bind("from", filter.from())
                .bind("toExclusive", toExclusive)
                .map((row, meta) -> RevenuePointDTO.builder()
                        .bucketDate(row.get("bucket_date", LocalDate.class))
                        .amount(row.get("amount", BigDecimal.class))
                        .build())
                .all();
    }

    @Override
    public Flux<PaymentMethodBreakdownDTO> getPaymentsByMethod(ReportsFilterDTO filter) {
        LocalDate toExclusive = filter.to().plusDays(1);

        String sql = """
            SELECT p.payment_method, SUM(p.amount) AS amount
            FROM payments p
            WHERE p.status = 'confirmed'
              AND p.payment_at >= :from AND p.payment_at < :toExclusive
            GROUP BY p.payment_method
            ORDER BY amount DESC
            """;

        return client.sql(sql)
                .bind("from", filter.from())
                .bind("toExclusive", toExclusive)
                .map((row, meta) -> new Object[]{
                        row.get("payment_method", String.class),
                        row.get("amount", BigDecimal.class)
                })
                .all()
                .collectList()
                .flatMapMany(rows -> {
                    BigDecimal total = rows.stream()
                            .map(r -> (BigDecimal) r[1])
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return Flux.fromIterable(rows).map(r -> {
                        String method = (String) r[0];
                        BigDecimal amount = (BigDecimal) r[1];
                        BigDecimal pct = total.compareTo(BigDecimal.ZERO) == 0
                                ? BigDecimal.ZERO
                                : amount.divide(total, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
                        return PaymentMethodBreakdownDTO.builder()
                                .method(method)
                                .amount(amount)
                                .percentage(pct)
                                .build();
                    });
                });
    }

    @Override
    public Flux<TopCustomerDTO> getTopCustomers(ReportsFilterDTO filter, int limit) {
        LocalDate toExclusive = filter.to().plusDays(1);

        String sql = """
            SELECT
              us.user_no AS customer_no,
              COALESCE(inv_agg.invoice_count, 0) AS invoice_count,
              COALESCE(pay_agg.paid_amount, 0) AS paid_amount,
              COALESCE(inv_agg.outstanding, 0) AS outstanding_amount,
              pay_agg.last_payment_date
            FROM users us
            JOIN (
                SELECT customer_id, SUM(amount) AS paid_amount, MAX(payment_at)::date AS last_payment_date
                FROM payments
                WHERE status = 'confirmed' AND payment_at >= :from AND payment_at < :toExclusive
                GROUP BY customer_id
            ) pay_agg ON pay_agg.customer_id = us.id
            LEFT JOIN (
                SELECT cust_id, COUNT(*) AS invoice_count, SUM(total - amount_paid) AS outstanding
                FROM invoice
                GROUP BY cust_id
            ) inv_agg ON inv_agg.cust_id = us.id
            ORDER BY pay_agg.paid_amount DESC
            LIMIT :limit
            """;

        return client.sql(sql)
                .bind("from", filter.from())
                .bind("toExclusive", toExclusive)
                .bind("limit", limit)
                .map((row, meta) -> TopCustomerDTO.builder()
                        .customerNo(row.get("customer_no", Long.class))
                        .invoiceCount(row.get("invoice_count", Long.class))
                        .paidAmount(row.get("paid_amount", BigDecimal.class))
                        .outstandingAmount(row.get("outstanding_amount", BigDecimal.class))
                        .lastPaymentDate(row.get("last_payment_date", LocalDate.class))
                        .build())
                .all();
    }

    private BigDecimal pctChange(BigDecimal current, BigDecimal prior) {
        if (prior == null || prior.compareTo(BigDecimal.ZERO) == 0) {
            return current != null && current.compareTo(BigDecimal.ZERO) > 0
                    ? BigDecimal.valueOf(100)
                    : BigDecimal.ZERO;
        }
        return current.subtract(prior)
                .divide(prior, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }
}