package org.squidmin.java.spring.maven.pubsub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "org.squidmin.java.spring.maven.pubsub")
public class GoogleCloudPubsubApplication {

    public static void main(String[] args) {
        SpringApplication.run(GoogleCloudPubsubApplication.class, args);
    }

}
