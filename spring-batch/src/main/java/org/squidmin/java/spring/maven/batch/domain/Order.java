package org.squidmin.java.spring.maven.batch.domain;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Order domain model class.
 * Represents an input row (from DB).
 */
public record Order(
    Long id,
    String externalOrderId,
    String email,
    String currency,
    BigDecimal amount,
    OffsetDateTime createdAt,
    boolean cancelled
) {}
