package org.squidmin.java.spring.maven.pubsub.subscribe;

import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
public class ExampleSubscriber {

    @Bean
    public Consumer<Message<String>> ingest() {
        return message -> {
            System.out.println("Received message: " + message.getPayload());
            // Add your message processing logic here
        };
    }
}
