package org.squidmin.java.spring.maven.casestudies.casestudy1.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.MapJobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.transaction.PlatformTransactionManager;
import org.squidmin.java.spring.maven.casestudies.casestudy1.domain.NormalizedWidget;
import org.squidmin.java.spring.maven.casestudies.casestudy1.domain.Widget;
import org.squidmin.java.spring.maven.casestudies.casestudy1.io.JsonToAvroProcessor;
import org.squidmin.java.spring.maven.casestudies.casestudy1.listeners.JobLoggerListener;
import org.squidmin.java.spring.maven.casestudies.casestudy1.listeners.StepLoggerListener;
import org.squidmin.java.spring.maven.casestudies.casestudy1.policies.TransientSkips;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class BatchJobConfig {

    @Bean
    public JobRepository jobRepository(DataSource dataSource, PlatformTransactionManager transactionManager) throws Exception {
        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        factory.setDataSource(dataSource);
        factory.setTransactionManager(transactionManager);
        factory.setIsolationLevelForCreate("ISOLATION_REPEATABLE_READ");
        factory.setTablePrefix("BATCH_");
        factory.afterPropertiesSet();
        return factory.getObject();
    }

    @Bean
    public JobOperator jobOperator(JobExplorer jobExplorer, JobLauncher jobLauncher, JobRepository jobRepository, JobRegistry jobRegistry) {
        SimpleJobOperator jobOperator = new SimpleJobOperator();
        jobOperator.setJobExplorer(jobExplorer);
        jobOperator.setJobLauncher(jobLauncher);
        jobOperator.setJobRepository(jobRepository);
        jobOperator.setJobRegistry(jobRegistry);
        return jobOperator;
    }

    @Bean
    public JobRegistry jobRegistry() {
        return new MapJobRegistry();
    }

    @Bean
    public Step normalizeWidgetsStep(JobRepository jobRepository,
                                     PlatformTransactionManager txManager,
                                     JdbcPagingItemReader<Widget> widgetReader,
                                     JsonToAvroProcessor processor,
                                     ItemWriter<NormalizedWidget> widgetCompositeWriter,
                                     StepLoggerListener stepLoggerListener,
                                     TransientSkips skipPolicy) {

        return new StepBuilder("normalizeWidgetsStep", jobRepository)
            .<Widget, NormalizedWidget>chunk(500, txManager) // Process 500 items per chunk
            .reader(widgetReader)                         // Read items from the database
            .processor(processor)                         // Process items (normalize data)
            .writer(widgetCompositeWriter)                // Write to DB and CSV
            .faultTolerant()
            .skipPolicy(skipPolicy)                       // Skip invalid rows (e.g., missing currency)
            .retryLimit(5)                                // Retry transient errors up to 3 times
            .retry(ConcurrencyFailureException.class)     // Retry on database lock issues
            .listener(stepLoggerListener)                 // Log step execution details
            .build();
    }

    @Bean
    public Job normalizeWidgetsJob(JobRepository jobRepository,
                                   Step normalizeWidgetsStep,
                                   JobLoggerListener jobLoggerListener) {
        return new JobBuilder("normalizeWidgetsJob", jobRepository)
            .listener(jobLoggerListener)
            .start(normalizeWidgetsStep)
            .build();
    }

}
