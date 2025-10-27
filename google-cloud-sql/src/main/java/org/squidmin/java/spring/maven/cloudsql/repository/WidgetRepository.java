package org.squidmin.java.spring.maven.cloudsql.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.squidmin.java.spring.maven.cloudsql.domain.Widget;

import java.util.List;
import java.util.UUID;

public interface WidgetRepository extends JpaRepository<Widget, UUID> {
    List<Widget> findByNameContainingIgnoreCase(String q);
}
