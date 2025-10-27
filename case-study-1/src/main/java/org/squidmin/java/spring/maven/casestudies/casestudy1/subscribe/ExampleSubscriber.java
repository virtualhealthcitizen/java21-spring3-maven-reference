package org.squidmin.java.spring.maven.casestudies.casestudy1.subscribe;

import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class ExampleSubscriber {

    public Message<String> fetchNextMessage() {
        // Implement logic to fetch a single message from Pub/Sub
        // Return null when no more messages are available
        return null;
    }

}
