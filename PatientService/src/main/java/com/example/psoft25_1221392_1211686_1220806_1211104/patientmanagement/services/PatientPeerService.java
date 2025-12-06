package com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.services;

import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.model.Patient;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.repositories.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Service for cross-instance patient data retrieval and replication
 * Implements read-through fallback pattern: if data not found locally,
 * fetch from peer instances and cache locally
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PatientPeerService {

    private final PatientRepository patientRepository;
    private final RestTemplate restTemplate;
    
    @Value("${patient.peer.urls:}")
    private String peerUrlsCsv;
    
    @Value("${server.instance.id:patient-service-1}")
    private String instanceId;

    /**
     * Get list of peer instance URLs
     */
    private List<String> getPeerUrls() {
        if (peerUrlsCsv == null || peerUrlsCsv.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(peerUrlsCsv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    /**
     * Replicate patient from peer instance to local database
     * Called by replication listener when receiving events from other instances
     */
    @Transactional
    public void replicatePatientFromPeer(Long patientId, String patientNumber) {
        Optional<Patient> existing = patientRepository.findByPatientNumber(patientNumber);
        if (existing.isPresent()) {
            log.debug("Patient {} already exists locally, skipping replication", patientNumber);
            return;
        }
        for (String peerUrl : getPeerUrls()) {
            try {
                String url = peerUrl + "/api/patients/internal/replication/" + patientNumber;
                log.debug("Fetching patient {} from peer: {}", patientNumber, peerUrl);
                
                ResponseEntity<Patient> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    createRequestEntity(),
                    Patient.class
                );

                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    Patient patient = response.getBody();
                    patient.setId(null);
                    patient.setUser(null);
                    if (patient.getHealthConcerns() != null) {
                        patient.getHealthConcerns().forEach(hc -> {
                            hc.setId(null);
                            hc.setPatient(patient);
                        });
                    }
                    patientRepository.save(patient);
                    log.info("Successfully replicated patient {} from peer {}", patientNumber, peerUrl);
                    return;
                }
            } catch (RestClientException e) {
                log.warn("Failed to fetch patient {} from peer {}: {}", patientNumber, peerUrl, e.getMessage());
            }
        }
        
        log.error("Failed to replicate patient {} from any peer instance", patientNumber);
    }

    /**
     * Delete patient from local database (called when receiving delete event)
     * For eventual consistency, we mark patient as disabled instead of hard delete
     */
    @Transactional
    public void deletePatientLocally(Long patientId, String patientNumber) {
        Optional<Patient> patient = patientRepository.findByPatientNumber(patientNumber);
        if (patient.isPresent()) {
            Patient p = patient.get();
            p.setEnabled(false);
            patientRepository.save(p);
            log.info("Marked patient {} as disabled in local database (replication)", patientNumber);
        } else {
            log.debug("Patient {} not found locally, nothing to delete", patientNumber);
        }
    }

    /**
     * Read-through fallback: if patient not found locally, try peer instances
     * Returns patient from peer if found, and optionally caches it locally
     * Supports both ID and patientNumber lookup
     */
    public Optional<Patient> findPatientWithFallback(Long patientId, String patientNumber) {
        Optional<Patient> local = Optional.empty();
        if (patientId != null) {
            local = patientRepository.findById(patientId);
        } else if (patientNumber != null) {
            local = patientRepository.findByPatientNumber(patientNumber);
        }
        
        if (local.isPresent()) {
            return local;
        }
        String searchParam = patientNumber != null ? patientNumber : (patientId != null ? patientId.toString() : null);
        log.debug("Patient {} not found locally, trying peer instances: {}", searchParam, getPeerUrls());
        
        for (String peerUrl : getPeerUrls()) {
            try {
                String url;
                if (patientId != null) {
                    url = peerUrl + "/api/patients/internal/replication/id/" + patientId;
                } else if (patientNumber != null) {
                    url = peerUrl + "/api/patients/internal/replication/" + patientNumber;
                } else {
                    break;
                }
                
                ResponseEntity<Patient> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    createRequestEntity(),
                    Patient.class
                );

                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    Patient patient = response.getBody();
                    Optional<Patient> existing = patientRepository.findByPatientNumber(patient.getPatientNumber());
                    if (existing.isPresent()) {
                        log.debug("Patient {} already exists locally, skipping replication", patient.getPatientNumber());
                        return Optional.of(existing.get());
                    }
                    patient.setId(null);
                    patient.setUser(null);
                    if (patient.getHealthConcerns() != null) {
                        patient.getHealthConcerns().forEach(hc -> {
                            hc.setId(null);
                            hc.setPatient(patient);
                        });
                    }
                    Patient savedPatient = patientRepository.save(patient);
                    log.info("Fetched patient {} from peer {} and cached locally", searchParam, peerUrl);
                    return Optional.of(savedPatient);
                }
            } catch (RestClientException e) {
                log.warn("Failed to fetch patient {} from peer {}: {}", searchParam, peerUrl, e.getMessage());
            }
        }

        return Optional.empty();
    }

    /**
     * Create HTTP request entity with headers
     */
    private HttpEntity<Void> createRequestEntity() {
        return new HttpEntity<>(new HttpHeaders());
    }
}

