package com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.infrastructure.repositories.impl;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PhysicianServiceClient {

    @Qualifier("restTemplateWithAuth")
    @Autowired
    private RestTemplate restTemplate;

    @Value("${PHYSICIAN_SERVICE_URL}")
    private String physicianUrl;

    @Value("${physician.port}")
    private int physicianPort;

    @CircuitBreaker(name = "physicianService", fallbackMethod = "workingHoursFallback")
    public String getWorkingHours(String physicianNumber) {
        String url = "https://localhost:5000/api/physicians/workinghours/" + physicianNumber;
        return restTemplate.getForObject(url, String.class);
    }

    // Fallback when circuit is OPEN or call fails
    public String workingHoursFallback(String physicianNumber, Throwable ex) {
        throw new ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Physician service unavailable"
        );
    }
}
