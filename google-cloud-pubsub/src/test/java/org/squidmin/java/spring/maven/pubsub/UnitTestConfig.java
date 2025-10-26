package org.squidmin.java.spring.maven.pubsub;

import com.google.auth.oauth2.GoogleCredentials;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.squidmin.java.spring.maven.pubsub.config.GoogleCredentialsConfig;

import java.io.IOException;

@Configuration
@ActiveProfiles("test")
public class UnitTestConfig {

    @Bean
    public GoogleCredentialsConfig googleCredentials() throws IOException {
        GoogleCredentialsConfig mock = Mockito.mock(GoogleCredentialsConfig.class);
        GoogleCredentials credentialsMock = Mockito.mock(GoogleCredentials.class);
        Mockito.when(mock.applicationDefaultCredentials()).thenReturn(credentialsMock);
        return mock;
    }

}
