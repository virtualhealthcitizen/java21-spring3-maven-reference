package org.squidmin.java.spring.maven.casestudies.casestudy1;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest
@SpringBatchTest
public class PostgresToGcsJobIntegrationTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @Autowired
    private Job postgresToGcsJob;

    @Test
    @Sql(scripts = {"/init.sql", "/create_spring_batch_tables.sql"})
    void testBatchJob() throws Exception {
        jobRepositoryTestUtils.removeJobExecutions();
        JobParameters jobParameters = new JobParametersBuilder()
            .addLong("time", System.currentTimeMillis())
            .toJobParameters();

        jobLauncherTestUtils.setJob(postgresToGcsJob);
        var exec = jobLauncherTestUtils.launchJob(jobParameters);
        Assertions.assertThat(exec.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
        StepExecution step = exec.getStepExecutions().iterator().next();
        Assertions.assertThat(step.getReadCount()).isEqualTo(4);
        Assertions.assertThat(step.getFilterCount()).isEqualTo(0);
        Assertions.assertThat(step.getSkipCount()).isEqualTo(0);
        Assertions.assertThat(step.getWriteCount()).isEqualTo(4);
    }

}
