package org.squidmin.java.spring.maven.casestudies.casestudy1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.postgresql.util.PGobject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@SpringBatchTest
@Testcontainers
@Import(BatchJobIntegrationTest.TestSupport.class)
@Disabled
class BatchJobIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(BatchJobIntegrationTest.class);

    @Container
    static final PostgreSQLContainer<?> POSTGRES =
        new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
    }

    @Autowired
    private JdbcTemplate jdbc;
    @Autowired
    private Job normalizeWidgetsJob;
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @BeforeEach
    void setupSchemaAndData() {
        try {
            jdbc.execute("DELETE FROM widgets");

            jdbc.update("""
                    INSERT INTO widgets(id, name, meta, created_at)
                    VALUES (?, ?, ?, ?)""",
                UUID.randomUUID(),
                "Widget A",
                createJsonbObject("{\"color\": \"red\", \"size\": \"large\"}"),
                LocalDateTime.now(ZoneOffset.UTC));

            jdbc.update("""
                    INSERT INTO widgets(id, name, meta, created_at)
                    VALUES (?, ?, ?, ?)""",
                UUID.randomUUID(),
                "Widget B",
                createJsonbObject("{\"color\": \"blue\", \"size\": \"medium\"}"),
                LocalDateTime.now(ZoneOffset.UTC));

            int count = jdbc.queryForObject("SELECT COUNT(*) FROM widgets", Integer.class);
            log.info("Number of widgets in the database: {}", count);

            log.info("Test data inserted into widgets table.");
        } catch (Exception e) {
            log.error("Error setting up schema and data", e);
            throw e;
        }
    }

//    @AfterEach
//    void cleanup() {
//        jdbc.execute("DROP TABLE IF EXISTS widgets");
//    }

    @Test
    void job_end_to_end_processes_and_writes_data() throws Exception {
        try {
            jdbc.execute("DELETE FROM widgets");

            jdbc.update("""
                    INSERT INTO widgets(id, name, meta, created_at)
                    VALUES (?, ?, ?, ?)""",
                UUID.randomUUID(),
                "Widget A",
                createJsonbObject("{\"color\": \"red\", \"size\": \"large\"}"),
                LocalDateTime.now(ZoneOffset.UTC));

            jdbc.update("""
                    INSERT INTO widgets(id, name, meta, created_at)
                    VALUES (?, ?, ?, ?)""",
                UUID.randomUUID(),
                "Widget B",
                createJsonbObject("{\"color\": \"blue\", \"size\": \"medium\"}"),
                LocalDateTime.now(ZoneOffset.UTC));

            int count = jdbc.queryForObject("SELECT COUNT(*) FROM widgets", Integer.class);
            log.info("Number of widgets in the database: {}", count);

            log.info("Test data inserted into widgets table.");
        } catch (Exception e) {
            log.error("Error setting up schema and data", e);
            throw e;
        }

        JobParameters params = new JobParametersBuilder()
            .addLong("run.id", System.nanoTime())
            .toJobParameters();

        jobLauncherTestUtils.setJob(normalizeWidgetsJob);
        JobExecution exec = jobLauncherTestUtils.launchJob(params);

        assertThat(exec.getStatus()).isEqualTo(BatchStatus.COMPLETED);

        StepExecution step = exec.getStepExecutions().iterator().next();
        assertThat(step.getReadCount()).isEqualTo(2); // 2 rows read
        assertThat(step.getWriteCount()).isEqualTo(2); // 2 rows written

        // Verify data processing (mocked GCS writer can be verified separately)
    }

    private PGobject createJsonbObject(String json) {
        try {
            PGobject jsonbObject = new PGobject();
            jsonbObject.setType("jsonb");
            jsonbObject.setValue(json);
            return jsonbObject;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create JSONB object", e);
        }
    }

    static class TestSupport {
    }

}
