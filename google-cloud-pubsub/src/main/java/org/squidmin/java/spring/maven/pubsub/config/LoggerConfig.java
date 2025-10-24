package org.squidmin.java.spring.maven.pubsub.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggerConfig {

    @Bean
    public Logger log() { return LoggerFactory.getLogger("GoogleCloudPubsubLogger"); }

}
