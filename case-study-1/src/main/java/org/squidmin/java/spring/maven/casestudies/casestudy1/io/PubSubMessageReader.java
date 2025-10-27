package org.squidmin.java.spring.maven.casestudies.casestudy1.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.squidmin.java.spring.maven.casestudies.casestudy1.subscribe.ExampleSubscriber;

@Component
public class PubSubMessageReader implements ItemReader<Message<String>> {

    private static final Logger log = LoggerFactory.getLogger(PubSubMessageReader.class);

    private final ExampleSubscriber subscriber;

    public PubSubMessageReader(ExampleSubscriber subscriber) {
        this.subscriber = subscriber;
    }

    @Override
    public Message<String> read() {
        // Implement logic to fetch a single message from Pub/Sub
        // Return null when no more messages are available
        Message<String> message = subscriber.fetchNextMessage();
        log.info("Message: {}", message);
        return message;
    }

}
