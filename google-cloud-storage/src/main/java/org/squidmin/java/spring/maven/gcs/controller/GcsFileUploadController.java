package org.squidmin.java.spring.maven.gcs.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.squidmin.java.spring.maven.gcs.dto.ExampleResponse;
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
    public ResponseEntity<ExampleResponse> query(@RequestBody ExampleRequest request) throws IOException {
        URL url = gcsService.uploadAvro(request.getFilename(), request);
        return ResponseEntity.ok(ExampleResponse.builder().url(url).build());
    }

    @GetMapping("/download")
    public ResponseEntity<ExampleResponse> downloadFile(@RequestParam String filename) throws IOException {
        URL url = gcsService.downloadAvro(filename);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
            .body(ExampleResponse.builder().url(url).build());
    }

}
