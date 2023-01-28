package com.vincenzoracca.resttemplateretry.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@Slf4j
public class AppConfig {

    @Bean
    RestTemplate restTemplate(@Value("${rest-template-retry.max-attempts}") int retryMaxAttempts) {
        return new RestTemplateRetryable(retryMaxAttempts);
    }

}
