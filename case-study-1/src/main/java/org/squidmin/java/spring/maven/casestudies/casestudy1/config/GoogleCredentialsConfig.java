package org.squidmin.java.spring.maven.casestudies.casestudy1.config;

import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class GoogleCredentialsConfig {

    @Bean
    public GoogleCredentials applicationDefaultCredentials() throws IOException {
        GoogleCredentials googleCredentials = GoogleCredentials.getApplicationDefault()
            .createScoped("https://www.googleapis.com/auth/cloud-platform");
        googleCredentials.refreshIfExpired();
        return googleCredentials;
    }

}
