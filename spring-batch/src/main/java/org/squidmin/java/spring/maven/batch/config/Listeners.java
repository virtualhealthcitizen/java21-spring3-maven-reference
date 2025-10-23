package org.squidmin.java.spring.maven.batch.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.stereotype.Component;

public class Listeners {

    @Component
    public static class JobLoggerListener implements JobExecutionListener {
        private static final Logger log = LoggerFactory.getLogger(JobLoggerListener.class);

        @Override
        public void beforeJob(JobExecution jobExecution) {
            log.info("Job {} starting with params {}", jobExecution.getJobInstance().getJobName(), jobExecution.getJobParameters());
        }

        @Override
        public void afterJob(JobExecution jobExecution) {
            log.info("Job {} ended with status {} (readCount={}, writeCount={}, skipCount={})",
                jobExecution.getJobInstance().getJobName(),
                jobExecution.getStatus(),
                jobExecution.getStepExecutions().stream().mapToLong(StepExecution::getReadCount).sum(),
                jobExecution.getStepExecutions().stream().mapToLong(StepExecution::getWriteCount).sum(),
                jobExecution.getStepExecutions().stream().mapToLong(StepExecution::getSkipCount).sum());
        }
    }

    @Component
    public static class StepLoggerListener implements StepExecutionListener {
        private static final Logger log = LoggerFactory.getLogger(StepLoggerListener.class);

        @Override
        public void beforeStep(StepExecution stepExecution) {
            log.info("Step {} starting", stepExecution.getStepName());
        }

        @Override
        public ExitStatus afterStep(StepExecution stepExecution) {
            log.info("Step {} finished: reads={}, writes={}, skips={}",
                stepExecution.getStepName(),
                stepExecution.getReadCount(), stepExecution.getWriteCount(), stepExecution.getSkipCount());
            return stepExecution.getExitStatus();
        }
    }

}
