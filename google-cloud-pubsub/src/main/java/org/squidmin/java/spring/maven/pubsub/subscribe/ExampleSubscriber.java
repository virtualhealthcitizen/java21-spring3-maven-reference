package org.squidmin.java.spring.maven.pubsub.subscribe;

import org.slf4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
public class ExampleSubscriber {

    private final Logger log;

    public ExampleSubscriber(Logger log) {
        this.log = log;
    }

    @Bean
    public Consumer<Message<String>> ingest() {
        return message -> {
            log.info("Received message payload: {}", message.getPayload());
            // Add your message processing logic here
        };
    }

}
