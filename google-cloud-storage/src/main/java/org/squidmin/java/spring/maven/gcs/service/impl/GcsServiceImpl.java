package org.squidmin.java.spring.maven.gcs.service.impl;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.squidmin.java.spring.maven.gcs.config.GcsConfig;
import org.squidmin.java.spring.maven.gcs.dto.ExampleRequest;
import org.squidmin.java.spring.maven.gcs.service.GcsService;
import org.squidmin.java.spring.maven.gcs.util.AvroUtil;

import java.io.IOException;
import java.net.URL;
import java.util.UUID;
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

    public URL uploadAvro(String filename, ExampleRequest request) throws IOException {
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

    public URL downloadAvro(String filename) {
        BlobInfo blobInfo = BlobInfo.newBuilder(BlobId.of(gcsConfig.getBucketName(), filename)).build();

        return storage.signUrl(
            blobInfo,
            5, TimeUnit.MINUTES,
            Storage.SignUrlOption.withV4Signature()
        );
    }

}
