package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class PatientClient {

    private String getToken() {
        // ⚠Token fixo temporário
        return "eyJhbGciOiJIUzI1NiIsInR5cCI6..."; // JWT de teste
    }


    private final RestTemplate restTemplate;
    private final String baseUrl;

    @Autowired
    public PatientClient(RestTemplate restTemplate,
                         @Value("${patient.service.url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public boolean patientExists(Long id) {
        try {
            String url = baseUrl + "/api/patients/id/" + id + "/profile";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + getToken());
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<Void> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    Void.class
            );

            return response.getStatusCode().is2xxSuccessful();
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        } catch (Exception e) {
            System.err.println("Erro ao contactar PatientService: " + e.getMessage());
            return false;
        }
    }



}




