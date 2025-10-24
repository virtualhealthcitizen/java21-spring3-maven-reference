package org.squidmin.java.spring.maven.pubsub.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.squidmin.java.spring.maven.pubsub.UnitTestConfig;

@SpringBootTest(classes = {PubSubConfig.class, UnitTestConfig.class})
@ActiveProfiles("test")
public class PubSubConfigTest {

    @Autowired
    private PubSubConfig pubSubConfig;

    @Test
    void contextLoads() {
        System.out.println("Project ID: " + pubSubConfig.getProjectId());
        System.out.println("Topic: " + pubSubConfig.getTopic());
        System.out.println("Subscription: " + pubSubConfig.getSubscription());
        System.out.println("Role: " + pubSubConfig.getRole());
        System.out.println("Ordering Key: " + pubSubConfig.getOrderingKey());
        System.out.println("Max Retries: " + pubSubConfig.getMaxRetries());
    }

}
