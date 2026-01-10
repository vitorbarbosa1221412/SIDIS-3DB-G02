package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(PatientClient.class);

    private final RestTemplate restTemplate;
    private final String baseUrl;

    @Autowired
    public PatientClient(RestTemplate restTemplate,
                         @Value("${patient.service.url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    private String getToken() {
        // ⚠ Idealmente, usar um serviço de tokens ou passar o token do contexto de segurança
        return "eyJhbGciOiJIUzI1NiIsInR5cCI6...";
    }

    //  RESILIENCE4J
    @CircuitBreaker(name = "patientService", fallbackMethod = "fallbackPatientExists")
    @Retry(name = "patientService")
    public boolean patientExists(Long id) {
        String url = baseUrl + "/api/patients/id/" + id + "/profile";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + getToken());
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Void> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    Void.class
            );
            return response.getStatusCode().is2xxSuccessful();

        } catch (HttpClientErrorException.NotFound e) {
            // Lógica de Negócio: O serviço respondeu, mas o paciente não existe.
            // Retornamos false e NÃO ativamos o Circuit Breaker.
            log.warn("Paciente {} não encontrado no sistema remoto.", id);
            return false;
        }

    }

    // FALLBACK
    // É chamado quando o Retry esgota as tentativas OU o Circuit Breaker está ABERTO.
    public boolean fallbackPatientExists(Long id, Throwable t) {
        System.err.println("FALLBACK: Serviço de Pacientes indisponível. Erro: " + t.getMessage());

        // Lança exceção para o Postman mostrar erro 500
        throw new RuntimeException("ALERTA: O Serviço de Pacientes está em baixo! (Fallback Ativado)");
    }
}




