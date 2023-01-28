package com.vincenzoracca.resttemplateretry.util;

import org.springframework.classify.Classifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.policy.ExceptionClassifierRetryPolicy;
import org.springframework.retry.policy.NeverRetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.HashSet;
import java.util.Set;

public class CustomRetryTemplateBuilder {

    private final Set<HttpStatusCode> httpStatusRetry;

    private int retryMaxAttempts;

    public CustomRetryTemplateBuilder() {
        this.httpStatusRetry = new HashSet<>();
    }

    public CustomRetryTemplateBuilder withHttpStatus(HttpStatus httpStatus) {
        this.httpStatusRetry.add(httpStatus);
        return this;
    }

    public CustomRetryTemplateBuilder withRetryMaxAttempts(int retryMaxAttempts) {
        this.retryMaxAttempts = retryMaxAttempts;
        return this;
    }

    public RetryTemplate build() {
        if (this.httpStatusRetry.isEmpty()) {
            this.httpStatusRetry.addAll(getDefaults());
        }
        return createRetryTemplate();
    }

    private RetryTemplate createRetryTemplate() {
        RetryTemplate retry = new RetryTemplate();
        ExceptionClassifierRetryPolicy policy = new ExceptionClassifierRetryPolicy();
        policy.setExceptionClassifier(configureStatusCodeBasedRetryPolicy());
        retry.setRetryPolicy(policy);

        return retry;
    }

    private Classifier<Throwable, RetryPolicy> configureStatusCodeBasedRetryPolicy() {
        //one execution + 3 retries
        SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy(1 + this.retryMaxAttempts);
        NeverRetryPolicy neverRetryPolicy = new NeverRetryPolicy();

        return throwable -> {
            if (throwable instanceof HttpStatusCodeException httpException) {
                return getRetryPolicyForStatus(httpException.getStatusCode(), simpleRetryPolicy, neverRetryPolicy);
            }
            return neverRetryPolicy;
        };
    }

    private RetryPolicy getRetryPolicyForStatus(HttpStatusCode httpStatusCode, SimpleRetryPolicy simpleRetryPolicy, NeverRetryPolicy neverRetryPolicy) {

        if (this.httpStatusRetry.contains(httpStatusCode)) {
            return simpleRetryPolicy;
        }
        return neverRetryPolicy;
    }

    private Set<HttpStatusCode> getDefaults() {
        return Set.of(
                HttpStatusCode.valueOf(HttpStatus.SERVICE_UNAVAILABLE.value()),
                HttpStatusCode.valueOf(HttpStatus.BAD_GATEWAY.value()),
                HttpStatusCode.valueOf(HttpStatus.GATEWAY_TIMEOUT.value())
        );
    }
}
