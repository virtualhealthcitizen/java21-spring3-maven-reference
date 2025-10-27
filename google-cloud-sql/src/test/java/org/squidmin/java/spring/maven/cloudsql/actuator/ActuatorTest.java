package org.squidmin.java.spring.maven.cloudsql.actuator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "management.endpoints.web.exposure.include=health,info",
        // show details so JSON has components (optional)
        "management.endpoint.health.show-details=always"
    }
)
public class ActuatorTest {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate rest;

    @Test
    void health_isUp() {
        ResponseEntity<String> res =
            rest.getForEntity("http://localhost:" + port + "/actuator/health", String.class);
        assertThat(res.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(res.getBody()).contains("\"status\":\"UP\"");
    }

    @Test
    void info_isReachable() {
        ResponseEntity<String> res =
            rest.getForEntity("http://localhost:" + port + "/actuator/info", String.class);
        assertThat(res.getStatusCode().is2xxSuccessful()).isTrue();
    }

}
