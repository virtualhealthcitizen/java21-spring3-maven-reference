package org.squidmin.java.spring.maven.batch.domain;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Output entity / row.
 */
@NoArgsConstructor
@AllArgsConstructor
public class NormalizedOrder {

    private Long id;

    private String externalOrderId;

    private String email;

    private String currency;

    private BigDecimal amount;

    private OffsetDateTime createdAt;

    private boolean cancelled;

}
