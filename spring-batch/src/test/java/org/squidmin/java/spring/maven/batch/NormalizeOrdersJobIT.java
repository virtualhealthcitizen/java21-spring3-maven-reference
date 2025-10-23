package org.squidmin.java.spring.maven.batch;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@SpringBatchTest
@Testcontainers
@Import(NormalizeOrdersJobIT.TestSupport.class)
class NormalizeOrdersJobIT {

    // --- PostgreSQL Testcontainer ---
    @Container
    static final PostgreSQLContainer<?> POSTGRES =
        new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        r.add("spring.datasource.username", POSTGRES::getUsername);
        r.add("spring.datasource.password", POSTGRES::getPassword);
        r.add("spring.jpa.hibernate.ddl-auto", () -> "update"); // let JPA create target table
    }

    @Autowired
    JdbcTemplate jdbc;
    @Autowired
    Job normalizeOrdersJob;
    @Autowired
    JobLauncherTestUtils jobLauncherTestUtils;

//    @TempDir
    Path tempDir = Path.of("exports/");

    @BeforeEach
    void setupSchemaAndData() {
        // Point CSV export to an isolated temp dir per test
        System.setProperty("export.dir", tempDir.toString());

        // Source table
        jdbc.execute("""
            CREATE TABLE IF NOT EXISTS raw_orders(
              id BIGINT PRIMARY KEY,
              external_order_id VARCHAR(64) UNIQUE NOT NULL,
              email VARCHAR(255) NOT NULL,
              currency VARCHAR(8),
              amount NUMERIC(18,2),
              created_at TIMESTAMPTZ NOT NULL,
              cancelled BOOLEAN NOT NULL DEFAULT FALSE
            )""");

        jdbc.execute("DELETE FROM raw_orders");

        var now = OffsetDateTime.now();

        // Valid rows (should be written)
        jdbc.update("""
                INSERT INTO raw_orders(id, external_order_id, email, currency, amount, created_at, cancelled)
                VALUES (?,?,?,?,?,?,?)""",
            1L, "EXT-1", "Alice@example.com", "usd", new BigDecimal("12.34"), now.minusDays(1), false);
        jdbc.update("""
                INSERT INTO raw_orders(id, external_order_id, email, currency, amount, created_at, cancelled)
                VALUES (?,?,?,?,?,?,?)""",
            2L, "EXT-2", "bob@EXAMPLE.com", "EUR", new BigDecimal("50.00"), now.minusHours(3), false);

// Filtered by processor (cancelled)
        jdbc.update("""
                INSERT INTO raw_orders(id, external_order_id, email, currency, amount, created_at, cancelled)
                VALUES (?,?,?,?,?,?,?)""",
            3L, "EXT-3", "skip@example.com", "USD", new BigDecimal("1.00"), now.minusHours(2), true);

// Invalid (no currency) → processor throws IllegalArgumentException → skipped by skip policy
        jdbc.update("""
                INSERT INTO raw_orders(id, external_order_id, email, currency, amount, created_at, cancelled)
                VALUES (?,?,?,?,?,?,?)""",
            4L, "EXT-4", "bad@example.com", null, new BigDecimal("99.99"), now.minusHours(1), false);

        // Ensure target table exists if JPA DDL is disabled in your setup:
        jdbc.execute("""
            CREATE TABLE IF NOT EXISTS normalized_orders(
              id BIGINT PRIMARY KEY,
              external_order_id VARCHAR(64) UNIQUE NOT NULL,
              email VARCHAR(255) NOT NULL,
              currency VARCHAR(3) NOT NULL,
              amount NUMERIC(18,2) NOT NULL,
              created_at TIMESTAMPTZ NOT NULL,
              cancelled BOOLEAN NOT NULL
            )""");
    }

    @AfterEach
    void clearProps() {
        System.clearProperty("export.dir");
    }

    @Test
    void job_end_to_end_writes_expected_records_and_csv() throws Exception {
        // Unique params to allow repeated runs
        JobParameters params = new JobParametersBuilder()
            .addLong("run.id", System.nanoTime())
            .toJobParameters();

        jobLauncherTestUtils.setJob(normalizeOrdersJob);
        JobExecution exec = jobLauncherTestUtils.launchJob(params);

        // --- Job/step assertions ---
        assertThat(exec.getStatus()).isEqualTo(BatchStatus.COMPLETED);

        StepExecution step = exec.getStepExecutions().iterator().next();
        assertThat(step.getReadCount()).isEqualTo(4);    // 4 source rows
        assertThat(step.getFilterCount()).isEqualTo(1);  // 1 cancelled filtered (processor returned null)
        assertThat(step.getSkipCount()).isEqualTo(1);    // 1 invalid skipped by policy
        assertThat(step.getWriteCount()).isEqualTo(2);   // 2 valid written

        // --- DB assertions ---
        Integer outCount = jdbc.queryForObject("SELECT COUNT(*) FROM normalized_orders", Integer.class);
        assertThat(outCount).isEqualTo(2);

        List<Row> rows = jdbc.query("""
            SELECT id, external_order_id, email, currency, amount::text
            FROM normalized_orders ORDER BY id
            """, (rs, i) -> new Row(
            rs.getLong("id"),
            rs.getString("external_order_id"),
            rs.getString("email"),
            rs.getString("currency"),
            rs.getString("amount"))
        );

        assertThat(rows).hasSize(2);
        assertThat(rows.get(0).email).isEqualTo("alice@example.com"); // email normalized to lower
        assertThat(rows.get(0).currency).isEqualTo("USD");            // currency normalized to upper

        // --- CSV export assertions ---
        Path csv = tempDir.resolve("normalized_orders.csv");
        assertThat(Files.exists(csv)).isTrue();
        List<String> lines = Files.readAllLines(csv);
        assertThat(lines).hasSize(2);
        assertThat(String.join("\n", lines)).contains("EXT-1").contains("EXT-2");
    }

    // Small projection for assertions
    record Row(Long id, String extId, String email, String currency, String amount) {
    }

    /**
     * Test wiring for SpringBatchTest
     */
    static class TestSupport {
//        @Bean
//        JobLauncherTestUtils jobLauncherTestUtils() { return new JobLauncherTestUtils(); }
    }

}
