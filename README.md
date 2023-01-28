# Spring Boot - RestTemplate with centralized retries configuration.

## What it is
In this project I configured the RestTemplate with RetryTemplate to manage the retries. \
The retry policy is a custom policy to manage retries according to HTTP statuses. \
In addition, the retry configuration is centralized so the clients can use the RestTemplate class as they normally would,
transparently.

## How
I used the Decorator design pattern to enrich the normal behavior of the RestTemplate with RetryTemplate:

`public class RestTemplateRetryable extends RestTemplate`