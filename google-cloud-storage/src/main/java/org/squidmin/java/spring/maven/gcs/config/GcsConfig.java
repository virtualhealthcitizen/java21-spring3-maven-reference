package org.squidmin.java.spring.maven.gcs.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ImpersonatedCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.List;

@Configuration
@Getter
@Slf4j
public class GcsConfig {

    private final String projectId;
    private final String gcsPrefix;
    private final String bucketName;
    private final String batchUploadPrefix;
    private final String impersonationTarget;
    private final String accessToken;
    private final String gkmsKeyName;

    public GcsConfig(@Value("${spring.cloud.gcp.project-id:#{systemEnvironment['PROJECT_ID']}}") String projectId,
                     @Value("${gcp.storage.gcs-prefix:#{systemEnvironment['GCS_PREFIX']}}") String gcsPrefix,
                     @Value("${gcp.storage.bucket.name:#{systemEnvironment['GCS_BUCKET_NAME']}}") String bucketName,
                     @Value("${gcp.storage.bucket.batch-upload-prefix:#{systemEnvironment['BATCH_UPLOAD_PREFIX']}}") String batchUploadPrefix,
                     @Value("${gcp.auth.impersonation-target:#{systemEnvironment['IMPERSONATION_TARGET']}}") String impersonationTarget,
                     @Value("${gcp.auth.access-token:#{systemEnvironment['OAUTH_ACCESS_TOKEN']}}") String accessToken,
                     @Value("${gcp.kms.key-name:#{systemEnvironment['GKMS_KEY_NAME']}}") String gkmsKeyName) {

        this.projectId = projectId;
        this.gcsPrefix = gcsPrefix;
        this.bucketName = bucketName;
        this.batchUploadPrefix = batchUploadPrefix;
        this.impersonationTarget = impersonationTarget;
        this.accessToken = accessToken;
        this.gkmsKeyName = gkmsKeyName;

    }

    @Bean
    public Storage storage() throws IOException {
        GoogleCredentials userCreds = GoogleCredentials.getApplicationDefault()
            .createScoped("https://www.googleapis.com/auth/cloud-platform");

        ImpersonatedCredentials saCreds = ImpersonatedCredentials.create(
            userCreds,
            getImpersonationTarget(), // e.g. "gcs-cmek-test-sa@lofty-root-378503.iam.gserviceaccount.com"
            null, // delegates
            List.of("https://www.googleapis.com/auth/cloud-platform"),
            3600 // lifetime in seconds
        );

        return StorageOptions.newBuilder()
            .setProjectId(projectId)
            .setCredentials(saCreds)
            .build()
            .getService();
    }

}
