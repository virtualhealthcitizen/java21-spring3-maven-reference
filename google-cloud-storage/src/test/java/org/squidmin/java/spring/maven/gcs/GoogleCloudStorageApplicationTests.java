package org.squidmin.java.spring.maven.gcs;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = {UnitTestConfig.class})
@ActiveProfiles("test")
class GoogleCloudStorageApplicationTests {

    @Test
    void contextLoads() {
    }

}
