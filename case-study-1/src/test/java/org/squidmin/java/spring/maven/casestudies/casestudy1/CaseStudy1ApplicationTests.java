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
import org.squidmin.java.spring.maven.casestudies.casestudy1.io.GcsWriter;
import org.squidmin.java.spring.maven.casestudies.casestudy1.io.JsonToAvroProcessor;
import org.squidmin.java.spring.maven.casestudies.casestudy1.io.PostgresReader;
import org.squidmin.java.spring.maven.casestudies.casestudy1.listeners.JobLoggerListener;
import org.squidmin.java.spring.maven.casestudies.casestudy1.listeners.StepLoggerListener;
import org.squidmin.java.spring.maven.casestudies.casestudy1.policies.TransientSkips;
import org.squidmin.java.spring.maven.casestudies.casestudy1.repository.WidgetRepository;
import org.squidmin.java.spring.maven.casestudies.casestudy1.repository.impl.GcsRepositoryImpl;
import org.squidmin.java.spring.maven.casestudies.casestudy1.service.WidgetService;
import org.squidmin.java.spring.maven.casestudies.casestudy1.service.impl.GcsServiceImpl;
import org.squidmin.java.spring.maven.casestudies.casestudy1.subscribe.ExampleSubscriber;
import org.squidmin.java.spring.maven.casestudies.casestudy1.util.AvroUtil;

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
        validateValidators(validators);

        WidgetController widgetController = context.getBean(WidgetController.class);
        Assertions.assertNotNull(widgetController);

        PostgresReader postgresReader = context.getBean(PostgresReader.class);
        validatePostgresReader(postgresReader);

        JsonToAvroProcessor jsonToAvroProcessor = context.getBean(JsonToAvroProcessor.class);
        validateJsonToAvroProcessor(jsonToAvroProcessor);

        GcsWriter gcsWriter = context.getBean(GcsWriter.class);
        Assertions.assertNotNull(gcsWriter);

        JobLoggerListener jobLoggerListener = context.getBean(JobLoggerListener.class);
        Assertions.assertNotNull(jobLoggerListener);

        StepLoggerListener stepLoggerListener = context.getBean(StepLoggerListener.class);
        Assertions.assertNotNull(stepLoggerListener);

        SkipPolicies.TransientSkips transientSkips = context.getBean(SkipPolicies.TransientSkips.class);
        Assertions.assertNotNull(transientSkips);

        TransientSkips _transientSkips = context.getBean(TransientSkips.class);
        Assertions.assertNotNull(_transientSkips);

        AvroUtil avroUtil = context.getBean(AvroUtil.class);
        Assertions.assertNotNull(avroUtil);

        GcsRepositoryImpl gcsRepository = context.getBean(GcsRepositoryImpl.class);
        Assertions.assertNotNull(gcsRepository);

        GcsServiceImpl gcsService = context.getBean(GcsServiceImpl.class);
        Assertions.assertNotNull(gcsService);

        WidgetRepository widgetRepository = context.getBean(WidgetRepository.class);
        Assertions.assertNotNull(widgetRepository);

        WidgetService widgetService = context.getBean(WidgetService.class);
        Assertions.assertNotNull(widgetService);

        WidgetController controller = context.getBean(WidgetController.class);
        Assertions.assertNotNull(controller);

        ExampleSubscriber exampleSubscriber = context.getBean(ExampleSubscriber.class);
        Assertions.assertNotNull(exampleSubscriber);
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

    private void validateValidators(Validators validators) {
        Assertions.assertNotNull(validators);
        Validator validator = validators.validator();
        Assertions.assertNotNull(validator);
        ExecutableValidator executableValidator = validator.forExecutables();
        Assertions.assertNotNull(executableValidator);
    }

    private void validatePostgresReader(PostgresReader postgresReader) {
        Assertions.assertNotNull(postgresReader);
        JdbcPagingItemReader<Widget> widgetJdbcPagingItemReader = postgresReader.widgetReader(dataSource);
        Assertions.assertNotNull(widgetJdbcPagingItemReader);
        Assertions.assertEquals("widgetReader", widgetJdbcPagingItemReader.getName());
    }

    private void validateJsonToAvroProcessor(JsonToAvroProcessor jsonToAvroProcessor) {
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
    }

}
