package org.squidmin.java.spring.maven.casestudies.casestudy1.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.squidmin.java.spring.maven.casestudies.casestudy1.domain.Widget;
import org.squidmin.java.spring.maven.casestudies.casestudy1.io.GcsWriter;
import org.squidmin.java.spring.maven.casestudies.casestudy1.io.PostgresReader;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    public BatchConfig(JobRepository jobRepository,
                       PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }

    @Bean
    public Step postgresToGcsStep(PostgresReader reader,
                                  ItemProcessor<Widget, byte[]> processor,
                                  GcsWriter writer) {
        return new StepBuilder("postgresToGcsStep", jobRepository)
            .<Widget, byte[]>chunk(10, transactionManager)
            .reader(reader)
            .processor(processor)
            .writer(writer)
            .build();
    }

    @Bean
    public Job postgresToGcsJob(Step postgresToGcsStep) {
        return new JobBuilder("postgresToGcsJob", jobRepository)
            .start(postgresToGcsStep)
            .build();
    }

}
