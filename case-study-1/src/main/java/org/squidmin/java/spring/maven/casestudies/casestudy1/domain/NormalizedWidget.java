package org.squidmin.java.spring.maven.casestudies.casestudy1.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "normalized_widget")
public class NormalizedWidget {

    @Id
    private Long id; // same as source for idempotency

    @Column(nullable = false, unique = true)
    private String externalOrderId;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private boolean cancelled;

    protected NormalizedWidget() {}

    public NormalizedWidget(Long id,
                            String name,
                            String externalId,
                            OffsetDateTime createdAt,
                            boolean cancelled) {

        this.id = id;
        this.name = name;
        this.externalOrderId = externalId;
        this.createdAt = createdAt;
        this.cancelled = cancelled;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }

    public String getExternalOrderId() { return externalOrderId; }

    public void setExternalOrderId(String externalOrderId) { this.externalOrderId = externalOrderId; }

    public OffsetDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public boolean isCancelled() { return cancelled; }

    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }

}

