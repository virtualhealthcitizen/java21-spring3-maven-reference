package org.squidmin.java.spring.maven.gcs.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.squidmin.java.spring.maven.gcs.dto.BatchFileUploadRequest;
import org.squidmin.java.spring.maven.gcs.dto.BatchFileUploadResponse;
import org.squidmin.java.spring.maven.gcs.dto.FileUploadRequest;
import org.squidmin.java.spring.maven.gcs.dto.FileUploadResponse;
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
    public ResponseEntity<FileUploadResponse> uploadFile(@RequestBody FileUploadRequest request) throws IOException {
        URL url = gcsServiceImpl.uploadAvro(request.getFilename(), request);
        return ResponseEntity.ok(FileUploadResponse.builder().url(url).build());
    }

    @PostMapping(
        value = "/batch-upload",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<BatchFileUploadResponse> batchUpload(@RequestBody BatchFileUploadRequest request) {
        BatchFileUploadResponse res = gcsServiceImpl.batchUpload(request);
        return ResponseEntity.ok(res);
    }

    @PostMapping(
        value = "/batch-update-gcs-metadata",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<BatchFileUploadResponse> batchUpdateGcsMetadata() {
        BatchFileUploadResponse res = gcsServiceImpl.batchUpdateGcsMetadata();
        return ResponseEntity.ok(res);
    }

    @GetMapping("/download")
    public ResponseEntity<FileUploadResponse> downloadFile(@RequestParam String filename) {
        URL url = gcsServiceImpl.downloadAvro(filename);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
            .body(FileUploadResponse.builder().url(url).build());
    }

}
