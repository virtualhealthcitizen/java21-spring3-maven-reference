package org.squidmin.java.spring.maven.casestudies.casestudy1.io;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import org.squidmin.java.spring.maven.casestudies.casestudy1.domain.NormalizedWidget;

@Component
public class GcsWriter implements ItemWriter<NormalizedWidget> {

    @Override
    public void write(Chunk<? extends NormalizedWidget> chunk) {

    }

}
