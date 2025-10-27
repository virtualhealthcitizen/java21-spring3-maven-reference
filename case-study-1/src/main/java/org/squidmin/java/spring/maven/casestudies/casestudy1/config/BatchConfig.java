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
import org.springframework.messaging.Message;
import org.springframework.transaction.PlatformTransactionManager;
import org.squidmin.java.spring.maven.casestudies.casestudy1.io.GcsAvroWriter;
import org.squidmin.java.spring.maven.casestudies.casestudy1.io.PubSubMessageReader;

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
    public Step pubSubToGcsStep(PubSubMessageReader reader,
                                ItemProcessor<Message<String>, byte[]> processor,
                                GcsAvroWriter writer) {
        return new StepBuilder("pubSubToGcsStep", jobRepository)
            .<Message<String>, byte[]>chunk(10, transactionManager)
            .reader(reader)
            .processor(processor)
            .writer(writer)
            .build();
    }

    @Bean
    public Job pubSubToGcsJob(Step pubSubToGcsStep) {
        return new JobBuilder("pubSubToGcsJob", jobRepository)
            .start(pubSubToGcsStep)
            .build();
    }

}
