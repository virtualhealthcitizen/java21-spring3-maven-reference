package org.squidmin.java.spring.maven.casestudies.casestudy1.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.squidmin.java.spring.maven.casestudies.casestudy1.domain.Widget;
import org.squidmin.java.spring.maven.casestudies.casestudy1.service.WidgetService;

import java.util.List;
import java.util.UUID;

@RestController("case-study-1/api/widgets")
public class WidgetController {

    private static final Logger log = LoggerFactory.getLogger(WidgetController.class);

    private final WidgetService widgetService;

    public WidgetController(WidgetService widgetService) {
        this.widgetService = widgetService;
    }

    @GetMapping
    public List<Widget> list(@RequestParam(name = "q", required = false) String q) {
        return widgetService.searchByName(q);
    }

    @GetMapping("{id}")
    public ResponseEntity<Widget> get(@PathVariable UUID id) {
        return widgetService.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

}
