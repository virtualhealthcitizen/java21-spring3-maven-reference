package org.squidmin.java.spring.maven.casestudies.casestudy1.service;

import org.springframework.stereotype.Service;
import org.squidmin.java.spring.maven.casestudies.casestudy1.domain.Widget;
import org.squidmin.java.spring.maven.casestudies.casestudy1.repository.WidgetRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class WidgetService {

    private final WidgetRepository repo;

    public WidgetService(WidgetRepository repo) {
        this.repo = repo;
    }

    /**
     * Read all rows
     */
    public List<Widget> findAll() {
        return repo.findAll();
    }

    /**
     * Read by primary key
     */
    public Optional<Widget> findById(UUID id) {
        return repo.findById(id);
    }

    /**
     * Read by name (case-insensitive contains)
     */
    public List<Widget> searchByName(String q) {
        return (q == null || q.isBlank()) ? repo.findAll()
            : repo.findByNameContainingIgnoreCase(q);
    }

}
