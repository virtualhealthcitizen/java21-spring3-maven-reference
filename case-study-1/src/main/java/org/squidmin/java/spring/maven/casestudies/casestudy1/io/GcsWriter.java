package org.squidmin.java.spring.maven.casestudies.casestudy1.io;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
public class GcsWriter implements ItemWriter<byte[]> {

    @Override
    public void write(Chunk<? extends byte[]> chunk) {
        // Write records to GCS in Avro format
    }

}
