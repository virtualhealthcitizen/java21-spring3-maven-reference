package org.squidmin.java.spring.maven.casestudies.casestudy1.io;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.squidmin.java.spring.maven.casestudies.casestudy1.domain.Widget;
import org.squidmin.java.spring.maven.casestudies.casestudy1.util.AvroUtil;

import java.util.List;

@Component
public class JsonToAvroProcessor implements ItemProcessor<Message<String>, byte[]> {

    private final AvroUtil avroUtil;
    private final ObjectMapper objectMapper;

    public JsonToAvroProcessor(AvroUtil avroUtil, ObjectMapper objectMapper) {
        this.avroUtil = avroUtil;
        this.objectMapper = objectMapper;
    }

    @Override
    public byte[] process(Message<String> item) throws Exception {
        List<Widget> widgets = objectMapper.readValue(item.getPayload(), new TypeReference<>() {});
        return avroUtil.serializeToAvro(widgets);
    }

}
