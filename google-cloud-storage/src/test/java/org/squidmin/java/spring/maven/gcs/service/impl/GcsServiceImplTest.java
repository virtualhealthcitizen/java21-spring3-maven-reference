package org.squidmin.java.spring.maven.gcs.service.impl;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ImpersonatedCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.context.ActiveProfiles;
import org.squidmin.java.spring.maven.gcs.GcsModuleTestUtil;
import org.squidmin.java.spring.maven.gcs.config.GcsConfig;
import org.squidmin.java.spring.maven.gcs.dto.ExampleRequest;
import org.squidmin.java.spring.maven.gcs.dto.ExampleUploadItem;
import org.squidmin.java.spring.maven.gcs.util.AvroUtil;

import java.lang.reflect.Field;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

@ActiveProfiles("test")
class GcsServiceImplTest {

    @Mock
    private GcsConfig gcsConfig;

    @Mock
    private AvroUtil avroUtil;

    @Mock
    private Storage storage;

    @Captor
    private ArgumentCaptor<BlobInfo> blobInfoCaptor;

    @InjectMocks
    private GcsServiceImpl gcsServiceImpl;

    @Mock
    private GoogleCredentials googleCredentials;

    @Mock
    private ImpersonatedCredentials impersonatedCredentials;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        Mockito.when(gcsConfig.getBucketName()).thenReturn("test-bucket");
        Mockito.when(gcsConfig.getProjectId()).thenReturn("test-project");
        Mockito.when(gcsConfig.getGkmsKeyName()).thenReturn("test-kms-key");

        Field field = GcsServiceImpl.class.getDeclaredField("gcsConfig");
        field.setAccessible(true);
        gcsServiceImpl = Mockito.spy(new GcsServiceImpl(
                gcsConfig,
                storage,
                avroUtil
            )
        );
    }

    @Test
    void uploadAvro_shouldUploadAvroAndReturnSignedUrl() throws Exception {
        String signedUrl = "https://signed-url";

        ExampleUploadItem item = new ExampleUploadItem("1", "2023-01-01T00:00:00Z", "2023-01-02T00:00:00Z", "A", "B");
        ExampleRequest request = new ExampleRequest("test.avro", List.of(item));

        Blob mockBlob = Mockito.mock(Blob.class);
        byte[] mockAvroBytes = new byte[]{1, 2, 3}; // Mocked byte array for Avro serialization

        Mockito.when(avroUtil.serializeToAvro(Mockito.anyList())).thenReturn(mockAvroBytes); // Mock serialization
        Mockito.when(storage.create(Mockito.any(), Mockito.eq(mockAvroBytes), Mockito.any())).thenReturn(mockBlob);
        Mockito.when(storage.signUrl(Mockito.any(), Mockito.eq(5L), Mockito.eq(TimeUnit.MINUTES), Mockito.any()))
            .thenReturn(URI.create(signedUrl).toURL());

        Mockito.when(avroUtil.getAvroSchema()).thenReturn(GcsModuleTestUtil.getAvroSchema("avro/schema.json"));

        URL resultUrl = gcsServiceImpl.uploadAvro("example.avro", request);

        Assertions.assertNotNull(resultUrl);
        Assertions.assertEquals(signedUrl, resultUrl.toString());

        Mockito.verify(storage).create(blobInfoCaptor.capture(), Mockito.eq(mockAvroBytes), Mockito.any(Storage.BlobTargetOption.class));
        BlobInfo capturedBlob = blobInfoCaptor.getValue();
        Assertions.assertTrue(capturedBlob.getBucket().contains("test-bucket"));
        Assertions.assertTrue(capturedBlob.getName().contains("example.avro"));
        Assertions.assertEquals("application/avro", capturedBlob.getContentType());
    }

}
