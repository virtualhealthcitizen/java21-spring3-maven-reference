package org.squidmin.java.spring.maven.casestudies.casestudy1.config;

import org.springframework.batch.core.step.skip.AlwaysSkipItemSkipPolicy;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.stereotype.Component;

@Component
public class SkipPolicies {

    /** Example: skip common transient/validation issues; fail on others. */
    @Component
    public static class TransientSkips implements SkipPolicy {
        @Override
        public boolean shouldSkip(Throwable t, long skipCount) {
            if (t instanceof IllegalArgumentException) {
                return true; // invalid row content
            } else if (t instanceof java.sql.SQLTransientException) {
                return true; // transient DB issues
            } else {
                return false;
            }
        }
    }

    /** Drop-in to skip everything (for quick tests) */
    @Component
    public static class SkipAll extends AlwaysSkipItemSkipPolicy {}

}
