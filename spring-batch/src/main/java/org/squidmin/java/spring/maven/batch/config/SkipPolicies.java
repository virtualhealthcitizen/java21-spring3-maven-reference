package org.squidmin.java.spring.maven.batch.config;

import org.springframework.batch.core.step.skip.AlwaysSkipItemSkipPolicy;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.stereotype.Component;

@Component
public class SkipPolicies {

    /** Example: skip common transient/validation issues; fail on others. */
    @Component
    public static class TransientSkips implements SkipPolicy {
        @Override public boolean shouldSkip(Throwable t, long skipCount) {
            return switch (t) {
                case IllegalArgumentException e -> true; // invalid row content
                case java.sql.SQLTransientException e -> true; // transient DB issues
                default -> false;
            };
        }
    }

    /** Drop-in to skip everything (for quick tests) */
    @Component
    public static class SkipAll extends AlwaysSkipItemSkipPolicy {}

}
