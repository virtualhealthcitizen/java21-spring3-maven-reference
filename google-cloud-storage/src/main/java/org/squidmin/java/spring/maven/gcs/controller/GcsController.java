package org.squidmin.java.spring.maven.gcs.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.squidmin.java.spring.maven.gcs.dto.ExampleRequest;
import org.squidmin.java.spring.maven.gcs.dto.ExampleResponse;
import org.squidmin.java.spring.maven.gcs.service.impl.GcsServiceImpl;

import java.io.IOException;
import java.net.URL;

@RestController
@RequestMapping("/gcs")
public class GcsController {

    private final GcsServiceImpl gcsServiceImpl;

    public GcsController(GcsServiceImpl gcsServiceImpl) {
        this.gcsServiceImpl = gcsServiceImpl;
    }

    @PostMapping(
        value = "/upload",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ExampleResponse> uploadFile(@RequestBody ExampleRequest request) throws IOException {
        URL url = gcsServiceImpl.uploadAvro(request.getFilename(), request);
        return ResponseEntity.ok(ExampleResponse.builder().url(url).build());
    }

    @GetMapping("/download")
    public ResponseEntity<ExampleResponse> downloadFile(@RequestParam String filename) {
        URL url = gcsServiceImpl.downloadAvro(filename);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
            .body(ExampleResponse.builder().url(url).build());
    }

}
