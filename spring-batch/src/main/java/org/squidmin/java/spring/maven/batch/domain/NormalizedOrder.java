package org.squidmin.java.spring.maven.batch.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "normalized_orders")
public class NormalizedOrder {

    @Id
    private Long id; // same as source for idempotency

    @Column(nullable = false, unique = true)
    private String externalOrderId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, length = 3)
    private String currency; // ISO-4217 uppercase

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private boolean cancelled;

    protected NormalizedOrder() {}

    public NormalizedOrder(Long id, String externalOrderId, String email,
                           String currency, BigDecimal amount,
                           OffsetDateTime createdAt, boolean cancelled) {
        this.id = id;
        this.externalOrderId = externalOrderId;
        this.email = email;
        this.currency = currency;
        this.amount = amount;
        this.createdAt = createdAt;
        this.cancelled = cancelled;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getExternalOrderId() { return externalOrderId; }

    public void setExternalOrderId(String externalOrderId) { this.externalOrderId = externalOrderId; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getCurrency() { return currency; }

    public void setCurrency(String currency) { this.currency = currency; }

    public BigDecimal getAmount() { return amount; }

    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public OffsetDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public boolean isCancelled() { return cancelled; }

    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }

}
