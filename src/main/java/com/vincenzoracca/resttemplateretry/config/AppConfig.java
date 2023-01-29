package com.vincenzoracca.resttemplateretry.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    RestTemplate restTemplate(@Value("${rest-template-retry.max-attempts}") int retryMaxAttempts) {
        return new RestTemplateRetryable(retryMaxAttempts);
    }

}
