package org.squidmin.java.spring.maven.casestudies.casestudy1.subscribe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.squidmin.java.spring.maven.casestudies.casestudy1.dto.WidgetPayload;
import org.squidmin.java.spring.maven.casestudies.casestudy1.util.Loggable;

import java.util.function.Consumer;

@Component
@Loggable
public class ExampleSubscriber {

    private final ObjectMapper objectMapper;

    private Logger log;

    public ExampleSubscriber(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean
    public Consumer<Message<String>> ingest() {
        return message -> {
            try {
                WidgetPayload payload = objectMapper.readValue(message.getPayload(), WidgetPayload.class);
                log.info("Received message payload: {}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(payload));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        };
    }

}
