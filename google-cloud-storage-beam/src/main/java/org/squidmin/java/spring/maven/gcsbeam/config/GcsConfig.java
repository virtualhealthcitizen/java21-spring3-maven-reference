package org.squidmin.java.spring.maven.gcsbeam.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GcsConfig {

    private final String projectId;

    public GcsConfig(@Value("${spring.cloud.gcp.project-id}") String projectId) {
        this.projectId = projectId;
    }

}
