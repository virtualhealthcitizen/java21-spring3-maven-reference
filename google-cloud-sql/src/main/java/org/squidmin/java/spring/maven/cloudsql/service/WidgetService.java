package org.squidmin.java.spring.maven.cloudsql.service;

import org.springframework.stereotype.Service;
import org.squidmin.java.spring.maven.cloudsql.domain.Widget;
import org.squidmin.java.spring.maven.cloudsql.repository.WidgetRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class WidgetService {

    private final WidgetRepository repo;

    public WidgetService(WidgetRepository repo) {
        this.repo = repo;
    }

    public List<Widget> findAll() {
        return repo.findAll();
    }

    public Optional<Widget> findById(UUID id) {
        return repo.findById(id);
    }

    public List<Widget> searchByName(String q) {
        return (q == null || q.isBlank()) ? repo.findAll() : repo.findByNameContainingIgnoreCase(q);
    }

    public Widget save(Widget widget) {
        return repo.save(widget);
    }

}
