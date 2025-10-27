package org.squidmin.java.spring.maven.casestudies.casestudy1.io;

import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import org.squidmin.java.spring.maven.casestudies.casestudy1.config.GcsConfig;

import java.util.UUID;

@Component
public class GcsAvroWriter implements ItemWriter<byte[]> {

    private final Storage storage;
    private final GcsConfig gcsConfig;

    public GcsAvroWriter(Storage storage, GcsConfig gcsConfig) {
        this.storage = storage;
        this.gcsConfig = gcsConfig;
    }

    @Override
    public void write(Chunk<? extends byte[]> chunk) {
        for (byte[] avroBytes : chunk.getItems()) {
            String filename = "message_" + UUID.randomUUID() + ".avro";
            BlobInfo blobInfo = BlobInfo.newBuilder(gcsConfig.getBucketName(), filename)
                .setContentType("application/avro")
                .build();
            storage.create(blobInfo, avroBytes);
        }
    }

}
