package org.squidmin.java.spring.maven.gcs.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ImpersonatedCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.squidmin.java.spring.maven.gcs.config.GcsConfig;
import org.squidmin.java.spring.maven.gcs.dto.ExampleRequest;
import org.squidmin.java.spring.maven.gcs.dto.ExampleUploadItem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Getter
@Slf4j
public class GcsService {

    private final GcsConfig gcsConfig;

    public GcsService(GcsConfig gcsConfig) {
        this.gcsConfig = gcsConfig;
    }

    public URL uploadAvro(String filename, ExampleRequest request) throws IOException {
        byte[] avroBytes = serializeToAvro(request.getUploadItems());

        Storage storage = getStorageInstance();

        BlobInfo blobInfo = BlobInfo.newBuilder(BlobId.of(gcsConfig.getBucketName(), filename + "_" + UUID.randomUUID() + ".avro"))
            .setContentType("application/avro")
            .build();

        storage.create(
            blobInfo,
            avroBytes,
            Storage.BlobTargetOption.kmsKeyName(gcsConfig.getGkmsKeyName())
        );

        return storage.signUrl(blobInfo, 5, TimeUnit.MINUTES, Storage.SignUrlOption.withV4Signature());
    }

    public URL downloadAvro(String filename) throws IOException {
        Storage storage = getStorageInstance();

        BlobInfo blobInfo = BlobInfo.newBuilder(BlobId.of(gcsConfig.getBucketName(), filename)).build();

        return storage.signUrl(
            blobInfo,
            5, TimeUnit.MINUTES,
            Storage.SignUrlOption.withV4Signature()
        );
    }

    Storage getStorageInstance() throws IOException {
        GoogleCredentials userCreds = GoogleCredentials.getApplicationDefault()
            .createScoped("https://www.googleapis.com/auth/cloud-platform");

        ImpersonatedCredentials saCreds = ImpersonatedCredentials.create(
            userCreds,
            gcsConfig.getImpersonationTarget(), // e.g. "gcs-cmek-test-sa@lofty-root-378503.iam.gserviceaccount.com"
            null, // delegates
            List.of("https://www.googleapis.com/auth/cloud-platform"),
            3600 // lifetime in seconds
        );

        return StorageOptions.newBuilder()
            .setProjectId(gcsConfig.getProjectId())
            .setCredentials(saCreds)
            .build()
            .getService();
    }


    byte[] serializeToAvro(List<ExampleUploadItem> items) throws IOException {
        Schema schema = new Schema.Parser().parse(getAvroSchema());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GenericDatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<>(schema);
        DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<>(datumWriter);
        dataFileWriter.create(schema, out);

        for (ExampleUploadItem item : items) {
            GenericRecord record = new GenericData.Record(schema);
            record.put("id", item.getId());
            record.put("creationTimestamp", item.getCreationTimestamp());
            record.put("lastUpdateTimestamp", item.getLastUpdateTimestamp());
            record.put("columnA", item.getColumnA());
            record.put("columnB", item.getColumnB());
            dataFileWriter.append(record);
        }

        dataFileWriter.close();
        return out.toByteArray();
    }

    String getAvroSchema() throws IOException {
        String schema = Strings.EMPTY;
        try (InputStream is = GcsService.class.getClassLoader().getResourceAsStream("avro/schema.json")) {
            if (is != null) {
                schema = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            }
        }
        return schema;
    }

}
