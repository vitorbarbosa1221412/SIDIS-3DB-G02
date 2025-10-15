package com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "external.services")
public class ExternalServiceConfig {

    @Value("${physician.service.url}")
    private String physicianServiceUrl;

    @Value("${appointment.service.url}")
    private String appointmentServiceUrl;
    
    // Connection timeout in milliseconds
    private int connectionTimeout = 5000;
    private int readTimeout = 10000;

    public String getPhysicianServiceUrl() {
        return physicianServiceUrl;
    }

    public void setPhysicianServiceUrl(String physicianServiceUrl) {
        this.physicianServiceUrl = physicianServiceUrl;
    }

    public String getAppointmentServiceUrl() {
        return appointmentServiceUrl;
    }

    public void setAppointmentServiceUrl(String appointmentServiceUrl) {
        this.appointmentServiceUrl = appointmentServiceUrl;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }
}


