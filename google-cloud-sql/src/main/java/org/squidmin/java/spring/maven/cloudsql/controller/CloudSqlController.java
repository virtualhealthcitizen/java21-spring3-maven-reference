package org.squidmin.java.spring.maven.cloudsql.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cloud-sql")
public class CloudSqlController {

    public CloudSqlController() {}

    @GetMapping(
        value = "/insert-rows",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> query() {
        return ResponseEntity.ok("Placeholder response from Cloud SQL Controller");
    }

}
