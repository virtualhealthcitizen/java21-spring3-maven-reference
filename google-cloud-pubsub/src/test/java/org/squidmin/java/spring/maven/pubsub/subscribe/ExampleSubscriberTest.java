package org.squidmin.java.spring.maven.pubsub.subscribe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.ActiveProfiles;
import org.squidmin.java.spring.maven.pubsub.dto.ExampleInboundMessage;
import org.squidmin.java.spring.maven.pubsub.dto.PingMessagePayload;
import org.squidmin.java.spring.maven.pubsub.model.PingMessage;

import java.util.List;
import java.util.UUID;

@ActiveProfiles("test")
public class ExampleSubscriberTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private ExampleSubscriber subscriber;

    private Logger log;

    @BeforeEach
    void beforeEach() {
        log = Mockito.mock(Logger.class);
        subscriber = new ExampleSubscriber(log);
    }

    @Test
    void testIngest() throws JsonProcessingException {
        ExampleInboundMessage inboundMessage = new PingMessagePayload(
            UUID.randomUUID().toString(),
            "ping",
            List.of(new PingMessage(
                "ping-1",
                String.valueOf(System.currentTimeMillis())
            ))
        );
        String inboundMessageStr = objectMapper.writeValueAsString(inboundMessage);
        var message = new GenericMessage<>(inboundMessageStr);
        var consumer = subscriber.ingest();
        consumer.accept(message);

        Mockito.verify(log).info("Received message payload: {}", inboundMessageStr);
    }

}
