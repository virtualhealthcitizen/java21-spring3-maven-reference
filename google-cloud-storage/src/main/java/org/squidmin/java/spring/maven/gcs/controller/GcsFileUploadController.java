package org.squidmin.java.spring.maven.gcs.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.squidmin.java.spring.maven.gcs.service.GcsService;
import org.squidmin.java.spring.maven.gcs.dto.ExampleRequest;

import java.io.IOException;
import java.net.URL;

@RestController
@RequestMapping("/gcs")
public class GcsFileUploadController {

    private final GcsService gcsService;

    public GcsFileUploadController(GcsService gcsService) {
        this.gcsService = gcsService;
    }

    @PostMapping(
        value = "/upload",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Object> query(@RequestBody ExampleRequest request) throws IOException {
        URL url = gcsService.uploadAvro(request.getFilename(), request);
        return ResponseEntity.ok(url);

    }

}
