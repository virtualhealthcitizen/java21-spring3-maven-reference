package org.squidmin.java.spring.maven.cloudsql;

import com.google.auth.oauth2.GoogleCredentials;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

@Configuration
@ActiveProfiles("test")
public class UnitTestConfig {

    @Bean
    public GoogleCredentials googleCredentials() {
        return Mockito.mock(GoogleCredentials.class);
    }

}
