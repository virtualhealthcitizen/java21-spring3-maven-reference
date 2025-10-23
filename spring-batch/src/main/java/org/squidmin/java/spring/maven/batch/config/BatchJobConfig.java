package org.squidmin.java.spring.maven.batch.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.transaction.PlatformTransactionManager;
import org.squidmin.java.spring.maven.batch.domain.NormalizedOrder;
import org.squidmin.java.spring.maven.batch.domain.Order;
import org.squidmin.java.spring.maven.batch.io.OrderItemProcessor;
import org.squidmin.java.spring.maven.batch.listeners.JobLoggerListener;
import org.squidmin.java.spring.maven.batch.listeners.StepLoggerListener;
import org.squidmin.java.spring.maven.batch.policies.TransientSkips;

@Configuration
@EnableBatchProcessing
public class BatchJobConfig {

    @Bean
    public Step normalizeOrdersStep(JobRepository jobRepository,
                                    PlatformTransactionManager txManager,
                                    JdbcPagingItemReader<Order> customerOrderReader,
                                    OrderItemProcessor processor,
                                    ItemWriter<NormalizedOrder> normalizedOrderCompositeWriter,
                                    StepLoggerListener stepLoggerListener,
                                    TransientSkips skipPolicy) {

        return new StepBuilder("normalizeOrdersStep", jobRepository)
            .<Order, NormalizedOrder>chunk(500, txManager) // Process 500 items per chunk
            .reader(customerOrderReader)                  // Read items from the database
            .processor(processor)                         // Process items (normalize data)
            .writer(normalizedOrderCompositeWriter)       // Write to DB and CSV
            .faultTolerant()
            .skipPolicy(skipPolicy)                       // Skip invalid rows (e.g., missing currency)
            .retryLimit(3)                                // Retry transient errors up to 3 times
            .retry(CannotAcquireLockException.class)      // Retry on database lock issues
            .listener(stepLoggerListener)                 // Log step execution details
//            .stream(dupItemWriter)
            .build();
    }

    @Bean
    public Job normalizeOrdersJob(JobRepository jobRepository,
                                  Step normalizeOrdersStep,
                                  JobLoggerListener jobLoggerListener) {
        return new JobBuilder("normalizeOrdersJob", jobRepository)
            .listener(jobLoggerListener)
            .start(normalizeOrdersStep)
            .build();
    }

}
