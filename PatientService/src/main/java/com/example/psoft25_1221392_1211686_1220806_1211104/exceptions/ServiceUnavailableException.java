package com.example.psoft25_1221392_1211686_1220806_1211104.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when an external service is unavailable
 * Typically used when Circuit Breaker is OPEN or service is down
 */
@ResponseStatus(code = HttpStatus.SERVICE_UNAVAILABLE, reason = "Service Temporarily Unavailable")
public class ServiceUnavailableException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ServiceUnavailableException(String message) {
        super(message);
    }

    public ServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceUnavailableException(String serviceName, String operation) {
        super(String.format("%s service is temporarily unavailable. Operation: %s", serviceName, operation));
    }
}


