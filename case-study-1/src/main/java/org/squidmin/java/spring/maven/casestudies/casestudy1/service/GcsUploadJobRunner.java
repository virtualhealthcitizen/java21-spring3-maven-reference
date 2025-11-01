package org.squidmin.java.spring.maven.casestudies.casestudy1.service;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(name = "app.jobs.enabled", havingValue = "true", matchIfMissing = false)
@Profile("!test")
@Component
public class GcsUploadJobRunner implements CommandLineRunner {

    private final JobLauncher jobLauncher;
    private final Job postgresToGcsJob;

    public GcsUploadJobRunner(JobLauncher jobLauncher, Job postgresToGcsJob) {
        this.jobLauncher = jobLauncher;
        this.postgresToGcsJob = postgresToGcsJob;
    }

    @Override
    public void run(String... args) throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
            .addLong("startTime", System.currentTimeMillis())
            .toJobParameters();
        jobLauncher.run(postgresToGcsJob, jobParameters);
    }

}
