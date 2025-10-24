package org.squidmin.java.spring.maven.pubsub.config;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.squidmin.java.spring.maven.pubsub.UnitTestConfig;

@SpringBootTest(classes = {PubSubConfig.class, UnitTestConfig.class})
@ActiveProfiles("test")
@Slf4j
public class PubSubConfigTest {

    @Autowired
    private PubSubConfig pubSubConfig;

    @Test
    void contextLoads() {
        System.out.println("Project ID: " + pubSubConfig.getProjectId());
        System.out.println("Topic: " + pubSubConfig.getTopic());
        System.out.println("Subscription: " + pubSubConfig.getSubscription());
    }

}
