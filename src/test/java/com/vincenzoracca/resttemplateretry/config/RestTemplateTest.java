package com.vincenzoracca.resttemplateretry.config;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;


class RestTemplateTest {

    @Test
    void testRetryWithTreeFails() throws IOException {
        RestTemplate restTemplate = new AppConfig().restTemplate(3);

        try(MockWebServer mockWebServer = new MockWebServer()) {
            String expectedResponse = "expect that it works";
            mockWebServer.enqueue(new MockResponse().setResponseCode(429));
            mockWebServer.enqueue(new MockResponse().setResponseCode(502));
            mockWebServer.enqueue(new MockResponse().setResponseCode(429));
            mockWebServer.enqueue(new MockResponse().setResponseCode(200)
                    .setBody(expectedResponse));

            mockWebServer.start();

            HttpUrl url = mockWebServer.url("/test");
            String response = restTemplate.getForObject(url.uri(), String.class);
            assertThat(response).isEqualTo(expectedResponse);

            mockWebServer.shutdown();
        }

    }

    @Test
    void testRetryWithFourFails() throws IOException {
        RestTemplate restTemplate = new AppConfig().restTemplate(3);

        try(MockWebServer mockWebServer = new MockWebServer()) {
            String expectedResponse = "expect that it works";
            mockWebServer.enqueue(new MockResponse().setResponseCode(429));
            mockWebServer.enqueue(new MockResponse().setResponseCode(502));
            mockWebServer.enqueue(new MockResponse().setResponseCode(429));
            mockWebServer.enqueue(new MockResponse().setResponseCode(429));
            mockWebServer.enqueue(new MockResponse().setResponseCode(200)
                    .setBody(expectedResponse));

            mockWebServer.start();

            HttpUrl url = mockWebServer.url("/test");
            Assertions.assertThrows(HttpClientErrorException.TooManyRequests.class,
                    () -> restTemplate.getForObject(url.uri(), String.class));

            mockWebServer.shutdown();
        }
    }

}
