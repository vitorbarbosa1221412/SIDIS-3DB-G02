package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class PatientClient {

    private final WebClient webClient;

    // o URL base do PatientService vem do application.yml
    public PatientClient(@Value("${patient.service.url}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public boolean patientExists(Long id) {
        try {
            webClient.get()
                    .uri("/patients/" + id)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
            return true;
        } catch (WebClientResponseException e) {
            return e.getStatusCode() != HttpStatus.NOT_FOUND;
        } catch (Exception e) {
            return false;
        }
    }
}

