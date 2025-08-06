package org.squidmin.java.spring.maven.gcs.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ImpersonatedCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.apache.avro.Schema;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.squidmin.java.spring.maven.gcs.config.GcsConfig;
import org.squidmin.java.spring.maven.gcs.dto.ExampleRequest;
import org.squidmin.java.spring.maven.gcs.dto.ExampleUploadItem;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class GcsServiceTest {

    @Mock
    private GcsConfig gcsConfig;

    @Mock
    private Storage storage;

    @Captor
    private ArgumentCaptor<BlobInfo> blobInfoCaptor;

    @InjectMocks
    private GcsService gcsService;

    @Mock
    private GoogleCredentials googleCredentials;

    @Mock
    private ImpersonatedCredentials impersonatedCredentials;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        when(gcsConfig.getBucketName()).thenReturn("test-bucket");
        when(gcsConfig.getProjectId()).thenReturn("test-project");
        when(gcsConfig.getGkmsKeyName()).thenReturn("test-kms-key");

        // Override getStorageFromAccessToken() via reflection to return our mocked Storage
        Field field = GcsService.class.getDeclaredField("gcsConfig");
        field.setAccessible(true);
        gcsService = spy(new GcsService(gcsConfig));
        doReturn(storage).when(gcsService).getStorageInstance();
    }

    @Test
    void uploadAvro_shouldUploadAvroAndReturnSignedUrl() throws Exception {
        // Arrange
        ExampleUploadItem item = new ExampleUploadItem("1", "2023-01-01T00:00:00Z", "2023-01-02T00:00:00Z", "A", "B");
        ExampleRequest request = new ExampleRequest("test.avro", List.of(item));

        Blob mockBlob = mock(Blob.class);
        when(storage.create(any(), any(byte[].class), any())).thenReturn(mockBlob);
        when(storage.signUrl(any(), eq(5L), eq(TimeUnit.MINUTES), any())).thenReturn(new URL("https://signed-url"));

        // Act
        URL resultUrl = gcsService.uploadAvro("example.avro", request);

        // Assert
        assertNotNull(resultUrl);
        assertEquals("https://signed-url", resultUrl.toString());

        verify(storage).create(blobInfoCaptor.capture(), any(byte[].class), any(Storage.BlobTargetOption.class));
        BlobInfo capturedBlob = blobInfoCaptor.getValue();
        assertEquals("test-bucket", capturedBlob.getBucket());
        assertEquals("example.avro", capturedBlob.getName());
        assertEquals("application/avro", capturedBlob.getContentType());
    }

    @Test
    void getStorageFromAccessToken_shouldReturnStorageInstance() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Arrange
        GcsConfig gcsConfigMock = Mockito.mock(GcsConfig.class);
        gcsService = new GcsService(gcsConfigMock);

        when(gcsConfigMock.getBucketName()).thenReturn("test-bucket");
        when(gcsConfigMock.getProjectId()).thenReturn("test-project");
        when(gcsConfigMock.getImpersonationTarget())
            .thenReturn("test-sa@fake-project.iam.gserviceaccount.com");

        // Access and invoke the private method using reflection
        Method method = GcsService.class.getDeclaredMethod("getStorageInstance");
        method.setAccessible(true); // Make private method accessible
        Object storage = method.invoke(gcsService);

        // Verify config methods were called
        verify(gcsConfigMock).getProjectId();
        verify(gcsConfigMock).getImpersonationTarget();

        // Optionally, assert that the returned object is of the correct type
        Assertions.assertNotNull(storage);
        Assertions.assertTrue(storage instanceof com.google.cloud.storage.Storage);
    }


    @Test
    void serializeToAvro_shouldSerializeCorrectly() throws Exception {
        // Arrange
        ExampleUploadItem item = new ExampleUploadItem("123", "2023-01-01", "2023-01-02", "valA", "valB");
        List<ExampleUploadItem> items = List.of(item);

        // Act
        byte[] avroBytes = gcsService.serializeToAvro(items);

        // Assert
        assertNotNull(avroBytes);
        Schema schema = new Schema.Parser().parse(gcsService.getAvroSchema());
        Assertions.assertEquals(5, schema.getFields().size());
    }

}

