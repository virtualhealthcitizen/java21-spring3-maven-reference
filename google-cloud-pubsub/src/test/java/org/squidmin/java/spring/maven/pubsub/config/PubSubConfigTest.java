package org.squidmin.java.spring.maven.pubsub.config;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.squidmin.java.spring.maven.pubsub.UnitTestConfig;

@SpringBootTest(classes = {PubSubConfig.class, UnitTestConfig.class})
@ActiveProfiles("test")
public class PubSubConfigTest {

    private static final Logger log = LoggerFactory.getLogger(PubSubConfigTest.class);

    @Autowired
    private PubSubConfig pubSubConfig;

    @Test
    void contextLoads() {
        log.info("Project ID: {}", pubSubConfig.getProjectId());
        log.info("Topic: {}", pubSubConfig.getTopic());
        log.info("Subscription: {}", pubSubConfig.getSubscription());
        log.info("Role: {}", pubSubConfig.getRole());
        log.info("Ordering Key: {}", pubSubConfig.getOrderingKey());
        log.info("Max Retries: {}", pubSubConfig.getMaxRetries());
    }

}
