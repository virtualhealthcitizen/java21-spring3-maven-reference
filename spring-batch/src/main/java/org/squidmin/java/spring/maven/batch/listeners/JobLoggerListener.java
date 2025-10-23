package org.squidmin.java.spring.maven.batch.listeners;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class JobLoggerListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        System.out.println("Job '" + jobExecution.getJobInstance().getJobName() + "' starting.");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        System.out.println("Job '" + jobExecution.getJobInstance().getJobName() + "' finished with status: " + jobExecution.getStatus());
    }

}
