package org.squidmin.java.spring.maven.batch.io;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import org.squidmin.java.spring.maven.batch.domain.NormalizedOrder;
import org.squidmin.java.spring.maven.batch.domain.Order;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class OrderItemProcessor implements ItemProcessor<Order, NormalizedOrder> {

    @Override
    public NormalizedOrder process(Order in) {
        if (in.cancelled()) {
            // Example business rule: drop cancelled orders from normalization
            return null; // filtered by Spring Batch
        }
        var currency = normalizeCurrency(in.currency());
        var email = normalizeEmail(in.email());
        BigDecimal amount = normalizeAmount(in.amount());

        return new NormalizedOrder(
            in.id(),
            in.externalOrderId(),
            email,
            currency,
            amount,
            in.createdAt(),
            false
        );
    }

    private String normalizeCurrency(String v) {
        if (v == null || v.isBlank()) throw new IllegalArgumentException("Missing currency");
        return v.trim().toUpperCase();
    }

    private String normalizeEmail(String v) {
        if (v == null || !v.contains("@")) throw new IllegalArgumentException("Invalid email");
        return v.trim().toLowerCase();
    }

    private BigDecimal normalizeAmount(BigDecimal v) {
        if (v == null || v.signum() < 0) throw new IllegalArgumentException("Invalid amount");
        return v.setScale(2, RoundingMode.HALF_UP);
    }

}
