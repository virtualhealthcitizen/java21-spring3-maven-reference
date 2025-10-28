package org.squidmin.java.spring.maven.casestudies.casestudy1.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import org.squidmin.java.spring.maven.casestudies.casestudy1.domain.NormalizedWidget;
import org.squidmin.java.spring.maven.casestudies.casestudy1.domain.Widget;
import org.squidmin.java.spring.maven.casestudies.casestudy1.util.AvroUtil;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Random;
import java.util.UUID;

@Component
public class JsonToAvroProcessor implements ItemProcessor<Widget, NormalizedWidget> {

    private final AvroUtil avroUtil;
    private final ObjectMapper objectMapper;

    public JsonToAvroProcessor(AvroUtil avroUtil, ObjectMapper objectMapper) {
        this.avroUtil = avroUtil;
        this.objectMapper = objectMapper;
    }

    @Override
    public NormalizedWidget process(Widget in) {
        // Randomly fail to simulate transient errors
//        if (null == in.getMeta()) {
//            return null;
//        }
        // Convert widget to Avro format
        return new NormalizedWidget(
            new Random().nextLong(1000),
            UUID.randomUUID().toString(),
            LocalDateTime.now().atOffset(ZoneOffset.UTC),
            false
        );
    }

}
