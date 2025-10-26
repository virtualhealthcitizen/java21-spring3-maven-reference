package org.squidmin.java.spring.maven.pubsub;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = {GoogleCloudPubsubApplicationTests.class, UnitTestConfig.class})
@ActiveProfiles("test")
class GoogleCloudPubsubApplicationTests {

    @Test
    void contextLoads() {
    }

}
