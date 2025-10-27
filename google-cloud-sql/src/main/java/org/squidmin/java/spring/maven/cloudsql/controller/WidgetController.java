package org.squidmin.java.spring.maven.cloudsql.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.squidmin.java.spring.maven.cloudsql.service.WidgetService;
import org.squidmin.java.spring.maven.cloudsql.domain.Widget;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/cloud-sql/api/widgets")
public class WidgetController {

    private final WidgetService widgetService;

    public WidgetController(WidgetService widgetService) {
        this.widgetService = widgetService;
    }

    @GetMapping
    public List<Widget> list(@RequestParam(name = "q", required = false) String q) {
        return widgetService.searchByName(q);
    }

    /**
     * Get a widget by ID
     * @param id the widget ID
     * @return the widget if found, or 404 if not found
     */
    @GetMapping("{id}")
    public ResponseEntity<Widget> get(@PathVariable UUID id) {
        return widgetService.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(
        value = "/insert",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Widget> insert(@RequestBody Widget widget) {
        // Generate ID server-side if not provided
        if (widget.getId() == null) {
            widget.setId(UUID.randomUUID());
        }

        Widget saved = widgetService.save(widget);
        return ResponseEntity.ok(saved);
    }

}
