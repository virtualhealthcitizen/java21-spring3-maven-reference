package org.squidmin.java.spring.maven.casestudies.casestudy1.policies;

import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.ParseException;
import org.springframework.stereotype.Component;

@Component
public class TransientSkips implements SkipPolicy {

    @Override
    public boolean shouldSkip(Throwable t, long skipCount) throws SkipLimitExceededException {
        return (t instanceof ParseException && skipCount <= 10) || (t instanceof IllegalArgumentException);
    }

}
