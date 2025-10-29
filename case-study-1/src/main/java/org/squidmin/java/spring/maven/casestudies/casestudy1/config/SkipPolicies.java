package org.squidmin.java.spring.maven.casestudies.casestudy1.config;

import org.springframework.batch.core.step.skip.AlwaysSkipItemSkipPolicy;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.stereotype.Component;

import java.sql.SQLTransientException;

/**
 * Defines various skip policies for handling errors during batch processing.
 * These policies determine whether to skip certain exceptions or not.
 * You can customize these policies based on your application's requirements.
 * For example, you might want to skip transient errors but fail on validation errors.
 * Each policy is implemented as a Spring component for easy integration.
 *
 * @see org.springframework.batch.core.step.skip.SkipPolicy
 */
@Component
public class SkipPolicies {

    /**
     * Example: skip common transient/validation issues; fail on others.
     */
    @Component
    public static class TransientSkips implements SkipPolicy {
        @Override
        public boolean shouldSkip(Throwable t, long skipCount) {
            if (t instanceof IllegalArgumentException) {
                return true; // invalid row content
            } else return t instanceof SQLTransientException; // transient DB issues
        }
    }

    /**
     * Drop-in to skip everything (for quick tests)
     */
    @Component
    public static class SkipAll extends AlwaysSkipItemSkipPolicy {
    }

}
