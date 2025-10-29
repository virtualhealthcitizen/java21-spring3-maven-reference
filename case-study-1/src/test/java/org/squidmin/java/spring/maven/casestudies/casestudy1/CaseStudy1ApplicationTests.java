package org.squidmin.java.spring.maven.casestudies.casestudy1;

import jakarta.validation.Validator;
import jakarta.validation.executable.ExecutableValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.squidmin.java.spring.maven.casestudies.casestudy1.config.*;
import org.squidmin.java.spring.maven.casestudies.casestudy1.controller.WidgetController;
import org.squidmin.java.spring.maven.casestudies.casestudy1.domain.NormalizedWidget;
import org.squidmin.java.spring.maven.casestudies.casestudy1.domain.Widget;
import org.squidmin.java.spring.maven.casestudies.casestudy1.io.JsonToAvroProcessor;
import org.squidmin.java.spring.maven.casestudies.casestudy1.io.PostgresReader;
import org.squidmin.java.spring.maven.casestudies.casestudy1.listeners.JobLoggerListener;
import org.squidmin.java.spring.maven.casestudies.casestudy1.listeners.StepLoggerListener;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.HashMap;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CaseStudy1ApplicationTests {

    private final Logger log = LoggerFactory.getLogger(CaseStudy1ApplicationTests.class);

    @Autowired
    private ConfigurableApplicationContext context;

    @Autowired
    private DataSource dataSource;

    @Test
    void contextLoads() {
        BatchJobConfig batchJobConfig = context.getBean(BatchJobConfig.class);
        validateBatchJobConfig(batchJobConfig);

        GcsConfig gcsConfig = context.getBean(GcsConfig.class);
        validateGcsConfig(gcsConfig);

        PubSubConfig pubSubConfig = context.getBean(PubSubConfig.class);
        validatePubSubConfig(pubSubConfig);

        SkipPolicies skipPolicies = context.getBean(SkipPolicies.class);
        Assertions.assertNotNull(skipPolicies);

        Validators validators = context.getBean(Validators.class);
        Assertions.assertNotNull(validators);
        Validator validator = validators.validator();
        Assertions.assertNotNull(validator);
        ExecutableValidator executableValidator = validator.forExecutables();
        Assertions.assertNotNull(executableValidator);

        WidgetController widgetController = context.getBean(WidgetController.class);
        Assertions.assertNotNull(widgetController);

        PostgresReader postgresReader = context.getBean(PostgresReader.class);
        Assertions.assertNotNull(postgresReader);
        JdbcPagingItemReader<Widget> widgetJdbcPagingItemReader = postgresReader.widgetReader(dataSource);
        Assertions.assertNotNull(widgetJdbcPagingItemReader);
        Assertions.assertEquals("widgetReader", widgetJdbcPagingItemReader.getName());

        JsonToAvroProcessor jsonToAvroProcessor = context.getBean(JsonToAvroProcessor.class);
        Assertions.assertNotNull(jsonToAvroProcessor);
        NormalizedWidget testWidget = jsonToAvroProcessor.process(
            new Widget(
                UUID.randomUUID(),
                "Test Widget",
                Instant.now(),
                new HashMap<>()
            )
        );
        Assertions.assertNotNull(testWidget);
        Assertions.assertEquals("Test Widget", testWidget.getName());

        JobLoggerListener jobLoggerListener = context.getBean(JobLoggerListener.class);
        Assertions.assertNotNull(jobLoggerListener);

        StepLoggerListener stepLoggerListener = context.getBean(StepLoggerListener.class);
        Assertions.assertNotNull(stepLoggerListener);
    }

    private void validateBatchJobConfig(BatchJobConfig batchJobConfig) {
        JobRegistry jobRegistry = batchJobConfig.jobRegistry();
        jobRegistry.getJobNames().forEach(log::info);
        Assertions.assertNotNull(batchJobConfig);
        Assertions.assertNotNull(jobRegistry);
        Assertions.assertNotNull(jobRegistry.getJobNames());
        Assertions.assertTrue(0 < jobRegistry.getJobNames().size());
    }

    private void validateGcsConfig(GcsConfig gcsConfig) {
        Assertions.assertNotNull(gcsConfig);
        Assertions.assertEquals("gs://", gcsConfig.getGcsPrefix());
        Assertions.assertEquals("batch_uploads/", gcsConfig.getBatchUploadPrefix());
        Assertions.assertEquals("jm_lofty-root-cmek-test", gcsConfig.getBucketName());
    }

    private void validatePubSubConfig(PubSubConfig pubSubConfig) {
        Assertions.assertNotNull(pubSubConfig);
        Assertions.assertEquals("lofty-root-378503", pubSubConfig.getProjectId());
        Assertions.assertEquals("java21-spring3-maven-reference-topic", pubSubConfig.getTopic());
        Assertions.assertEquals("java21-spring3-maven-reference-subscription", pubSubConfig.getSubscription());
        Assertions.assertEquals("roles/pubsub.editor", pubSubConfig.getRole());
        Assertions.assertEquals("test-ordering-key", pubSubConfig.getOrderingKey());
        Assertions.assertEquals("5", pubSubConfig.getMaxRetries());
    }

}
