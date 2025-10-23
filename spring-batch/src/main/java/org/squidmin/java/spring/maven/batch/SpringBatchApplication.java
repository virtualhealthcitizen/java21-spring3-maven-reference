package org.squidmin.java.spring.maven.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication(scanBasePackages = "org.squidmin.java.spring.maven.batch")
@EntityScan(basePackages = "org.squidmin.java.spring.maven.batch.domain")
public class SpringBatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBatchApplication.class, args);
    }

}
