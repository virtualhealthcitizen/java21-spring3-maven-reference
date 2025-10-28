package org.squidmin.java.spring.maven.casestudies.casestudy1.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import org.squidmin.java.spring.maven.casestudies.casestudy1.domain.Widget;
import org.squidmin.java.spring.maven.casestudies.casestudy1.util.AvroUtil;

@Component
public class JsonToAvroProcessor implements ItemProcessor<Widget, byte[]> {

    private final AvroUtil avroUtil;
    private final ObjectMapper objectMapper;

    public JsonToAvroProcessor(AvroUtil avroUtil, ObjectMapper objectMapper) {
        this.avroUtil = avroUtil;
        this.objectMapper = objectMapper;
    }

    @Override
    public byte[] process(Widget item) {
        // Convert widget to Avro format
        return null;
    }

}
