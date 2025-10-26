package org.squidmin.java.spring.maven.gcs.service.impl;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.squidmin.java.spring.maven.gcs.config.GcsConfig;
import org.squidmin.java.spring.maven.gcs.dto.BatchFileUploadRequest;
import org.squidmin.java.spring.maven.gcs.dto.BatchFileUploadResponse;
import org.squidmin.java.spring.maven.gcs.dto.FileUploadRequest;
import org.squidmin.java.spring.maven.gcs.dto.UploadResult;
import org.squidmin.java.spring.maven.gcs.service.GcsService;
import org.squidmin.java.spring.maven.gcs.util.AvroUtil;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@Getter
@Slf4j
public class GcsServiceImpl implements GcsService {

    private final GcsConfig gcsConfig;
    private final Storage storage;
    private final AvroUtil avroUtil;

    public GcsServiceImpl(GcsConfig gcsConfig, Storage storage, AvroUtil avroUtil) {
        this.gcsConfig = gcsConfig;
        this.storage = storage;
        this.avroUtil = avroUtil;
    }

    public URL uploadAvro(String filename, FileUploadRequest request) throws IOException {
        byte[] avroBytes = avroUtil.serializeToAvro(request.getUploadItems());

        BlobInfo blobInfo = BlobInfo.newBuilder(BlobId.of(gcsConfig.getBucketName(), filename + "_" + UUID.randomUUID().toString().substring(0, 6) + ".avro"))
            .setContentType("application/avro")
            .build();

        storage.create(
            blobInfo,
            avroBytes,
            Storage.BlobTargetOption.kmsKeyName(gcsConfig.getGkmsKeyName())
        );

        return storage.signUrl(blobInfo, 5, TimeUnit.MINUTES, Storage.SignUrlOption.withV4Signature());
    }

    public BatchFileUploadResponse batchUpload(BatchFileUploadRequest request) {
        List<FileUploadRequest> items = request.getFileUploadRequests();
        int poolSize = Math.min(Math.max(2, Runtime.getRuntime().availableProcessors() * 2), Math.max(2, items.size()));

        try (ExecutorService pool = Executors.newFixedThreadPool(poolSize)) {
            try {
                List<CompletableFuture<UploadResult>> futures = items.stream()
                    .map(r -> CompletableFuture.supplyAsync(() -> {
                            try {
                                return doUpload(r);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }, pool)
                        .handle((res, ex) -> ex == null ? res
                            : UploadResult.failure(safeName(r), ex)))
                    .toList();

                List<UploadResult> results = futures.stream().map(CompletableFuture::join).toList();

                long success = results.stream().filter(UploadResult::isSuccess).count();
                long failure = results.size() - success;

                return BatchFileUploadResponse.builder()
                    .totalRequested(results.size())
                    .successCount((int) success)
                    .failureCount((int) failure)
                    .results(results)
                    .build();

            } finally {
                pool.shutdown();
            }
        }
    }

    private UploadResult doUpload(FileUploadRequest r) throws IOException {
        var filename = safeName(r);
        var signedUrl = uploadAvro(filename, r);
        var objectName = filenameForObject(filename);
        return UploadResult.success(filename, objectName, signedUrl.toString());
    }

    private String safeName(FileUploadRequest r) {
        return (r.getFilename() == null || r.getFilename().isBlank())
            ? "file-" + UUID.randomUUID()
            : r.getFilename();
    }

    private String filenameForObject(String base) {
        return base;  // Modify for name of object in GCS as needed.
    }

    public BatchFileUploadResponse batchUpdateGcsMetadata() {
        var batchUploadPrefix = gcsConfig.getBatchUploadPrefix();
        var gcsBucketName = gcsConfig.getBucketName();

        Map<String, String> newMetadata = new HashMap<>();
        newMetadata.put("keyToAddOrUpdate", "value");

        Page<Blob> blobs =
            storage.list(
                gcsBucketName,
                Storage.BlobListOption.prefix(batchUploadPrefix),
                Storage.BlobListOption.delimiter("/"));

        // Print blob metadata (keys and values available to update)
        for (Blob blob : blobs.iterateAll()) {
            log.info("Blob name: " + blob.getName());
            Map<String, String> metadata = blob.getMetadata();
            if (metadata != null) {
                for (Map.Entry<String, String> entry : metadata.entrySet()) {
                    log.info("  Metadata - Key: " + entry.getKey() + ", Value: " + entry.getValue());
                }
            } else {
                log.info("  No metadata found for this blob.");
            }
        }

        StorageBatch batchRequest = storage.batch();

        // Add all blobs with the given prefix to the batch request
        List<StorageBatchResult<Blob>> batchResults =
            blobs
                .streamAll()
                .map(blob -> batchRequest.update(blob.toBuilder().setMetadata(newMetadata).build()))
                .toList();

        // Execute the batch request
        batchRequest.submit();
        List<StorageException> failures =
            batchResults.stream()
                .map(
                    r -> {
                        try {
                            BlobInfo blob = r.get();
                            log.info("Updated metadata for blob: " + blob.getName());
                            return null;
                        } catch (StorageException e) {
                            return e;
                        }
                    })
                .filter(Objects::nonNull)
                .toList();

        log.info(
            (batchResults.size() - failures.size())
                + " blobs in bucket "
                + gcsBucketName
                + " with prefix '"
                + batchUploadPrefix
                + "' had their metadata updated successfully.");

        if (!failures.isEmpty()) {
            log.info("While processing, there were " + failures.size() + " failures");

            for (StorageException failure : failures) {
                failure.printStackTrace(System.out);
            }
        }

        return BatchFileUploadResponse.builder()
            .totalRequested(batchResults.size())
            .successCount(batchResults.size() - failures.size())
            .failureCount(failures.size())
            .build();
    }

    public URL downloadAvro(String filename) {
        BlobInfo blobInfo = BlobInfo.newBuilder(BlobId.of(gcsConfig.getBucketName(), filename)).build();

        return storage.signUrl(
            blobInfo,
            5, TimeUnit.MINUTES,
            Storage.SignUrlOption.withV4Signature()
        );
    }

}
