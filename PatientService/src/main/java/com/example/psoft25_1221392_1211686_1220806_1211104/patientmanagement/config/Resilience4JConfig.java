package com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Resilience4j Configuration
 * Configures Circuit Breaker, Retry, Timeout, and Bulkhead patterns
 * for inter-service communication resilience
 */
@Configuration
public class Resilience4JConfig {

    // Configuration constants
    private static final Duration TIMEOUT_DURATION = Duration.ofSeconds(10);
    private static final int MAX_RETRY_ATTEMPTS = 5;
    private static final float FAILURE_RATE_THRESHOLD = 25.0f; // 25%

    /**
     * Circuit Breaker Configuration
     * Opens circuit when failure rate exceeds threshold
     */
    @Bean
    public CircuitBreakerConfig circuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
                .failureRateThreshold(FAILURE_RATE_THRESHOLD)
                .waitDurationInOpenState(Duration.ofSeconds(30))
                .slidingWindowSize(10)
                .minimumNumberOfCalls(5)
                .permittedNumberOfCallsInHalfOpenState(3)
                .automaticTransitionFromOpenToHalfOpenEnabled(true)
                .recordExceptions(Exception.class)
                .build();
    }

    /**
     * Retry Configuration
     * Retries failed operations up to 5 times
     */
    @Bean
    public RetryConfig retryConfig() {
        return RetryConfig.custom()
                .maxAttempts(MAX_RETRY_ATTEMPTS)
                .waitDuration(Duration.ofMillis(500))
                .retryExceptions(Exception.class)
                .build();
    }

    /**
     * Time Limiter Configuration
     * Sets timeout to 10 seconds
     */
    @Bean
    public TimeLimiterConfig timeLimiterConfig() {
        return TimeLimiterConfig.custom()
                .timeoutDuration(TIMEOUT_DURATION)
                .cancelRunningFuture(true)
                .build();
    }

    /**
     * Bulkhead Configuration
     * Limits concurrent executions to prevent resource exhaustion
     */
    @Bean
    public BulkheadConfig bulkheadConfig() {
        return BulkheadConfig.custom()
                .maxConcurrentCalls(25)
                .maxWaitDuration(Duration.ofMillis(500))
                .build();
    }

    /**
     * Circuit Breaker Registry - allows creating named instances
     * When @CircuitBreaker(name = "appointmentService") is used, Spring Boot will
     * automatically create the instance using the default config from this registry
     */
    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        return CircuitBreakerRegistry.of(circuitBreakerConfig());
    }

    /**
     * Retry Registry - allows creating named instances
     * When @Retry(name = "appointmentService") is used, Spring Boot will
     * automatically create the instance using the default config from this registry
     */
    @Bean
    public RetryRegistry retryRegistry() {
        return RetryRegistry.of(retryConfig());
    }

    /**
     * Time Limiter Registry - allows creating named instances
     * When @TimeLimiter(name = "appointmentService") is used, Spring Boot will
     * automatically create the instance using the default config from this registry
     */
    @Bean
    public TimeLimiterRegistry timeLimiterRegistry() {
        return TimeLimiterRegistry.of(timeLimiterConfig());
    }

    /**
     * Bulkhead Registry - allows creating named instances
     * When @Bulkhead(name = "appointmentService") is used, Spring Boot will
     * automatically create the instance using the default config from this registry
     */
    @Bean
    public BulkheadRegistry bulkheadRegistry() {
        return BulkheadRegistry.of(bulkheadConfig());
    }

    /**
     * Customizer for Resilience4J Circuit Breaker Factory
     * Integrates all resilience patterns together
     */
    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .circuitBreakerConfig(circuitBreakerConfig())
                .timeLimiterConfig(timeLimiterConfig())
                .build());
    }
}

