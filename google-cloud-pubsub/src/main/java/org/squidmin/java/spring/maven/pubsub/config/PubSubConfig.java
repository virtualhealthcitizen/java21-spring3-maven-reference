package org.squidmin.java.spring.maven.pubsub.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PubSubConfig {

    private final String projectId;
    private final String topic;
    private final String subscription;
    private final String role;
    private final String orderingKey;
    private final String maxRetries;

    public PubSubConfig(@Value("${spring.cloud.gcp.project-id}") String projectId,
                        @Value("${gcp.pubsub.topic.name}") String topic,
                        @Value("${gcp.pubsub.subscription.name}") String subscription,
                        @Value("${gcp.pubsub.role.name:roles/pubsub.subscriber}") String role,
                        @Value("${gcp.pubsub.ordering-key:}") String orderingKey,
                        @Value("${gcp.pubsub.max-retries:5}") String maxRetries) {

        this.projectId = projectId;
        this.topic = topic;
        this.subscription = subscription;
        this.role = role;
        this.orderingKey = orderingKey;
        this.maxRetries = maxRetries;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getTopic() {
        return topic;
    }

    public String getSubscription() {
        return subscription;
    }

    public String getRole() {
        return role;
    }

    public String getOrderingKey() {
        return orderingKey;
    }

    public String getMaxRetries() {
        return maxRetries;
    }

//    @Bean
//    public PubSubSubscriber

}
