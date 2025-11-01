package org.squidmin.java.spring.maven.casestudies.casestudy1.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "widgets")
public class Widget {

    @Id
    @Column(name = "id", nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    // DB sets default now(); mark read-only so we don’t overwrite it
    @Column(name = "created_at", columnDefinition = "timestamptz", updatable = false, insertable = false)
    private Instant createdAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "meta", columnDefinition = "jsonb")
    private Map<String, Object> meta;

    public Widget() {}

    public Widget(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public Widget(UUID id, String name, Instant createdAt, Map<String, Object> meta) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.meta = meta;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Instant getCreatedAt() { return createdAt; }

    public Map<String, Object> getMeta() { return meta; }
    public void setMeta(Map<String, Object> meta) { this.meta = meta; }

}
