package org.squidmin.java.spring.maven.casestudies.casestudy1;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.squidmin.java.spring.maven.casestudies.casestudy1.config.BatchJobConfig;
import org.squidmin.java.spring.maven.casestudies.casestudy1.config.GcsConfig;
import org.squidmin.java.spring.maven.casestudies.casestudy1.config.PubSubConfig;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Slf4j
class CaseStudy1ApplicationTests {

    @Autowired
    private ConfigurableApplicationContext context;

    @Test
    void contextLoads() {
        BatchJobConfig batchJobConfig = context.getBean(BatchJobConfig.class);
        JobRegistry jobRegistry = batchJobConfig.jobRegistry();
        jobRegistry.getJobNames().forEach(System.out::println);
        Assertions.assertNotNull(batchJobConfig);
        Assertions.assertNotNull(jobRegistry);
        Assertions.assertNotNull(jobRegistry.getJobNames());

        GcsConfig gcsConfig = context.getBean(GcsConfig.class);
        Assertions.assertNotNull(gcsConfig);
        Assertions.assertEquals("gs://", gcsConfig.getGcsPrefix());
        Assertions.assertEquals("batch_uploads/", gcsConfig.getBatchUploadPrefix());
        Assertions.assertEquals("jm_lofty-root-cmek-test", gcsConfig.getBucketName());

        PubSubConfig pubSubConfig = context.getBean(PubSubConfig.class);
        Assertions.assertNotNull(pubSubConfig);
    }

}
