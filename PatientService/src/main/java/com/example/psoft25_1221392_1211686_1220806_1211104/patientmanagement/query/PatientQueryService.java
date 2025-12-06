package com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.query;

import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.model.Patient;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.repositories.PatientRepository;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.services.Page;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.services.PatientPeerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Query Service - Handles all read operations (CQRS Query Side)
 * Uses the existing Patient model and PatientRepository
 * Implements read-through fallback: if not found locally, query peer instances
 * This separation allows for optimized read operations and eventual consistency
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PatientQueryService {

    private final PatientRepository patientRepository;
    private final PatientPeerService patientPeerService;

    public ResponseEntity<Patient> getPatientById(Long id) {
        Optional<Patient> patient = patientRepository.findById(id);
        
        if (patient.isEmpty()) {
            log.debug("Patient with id {} not found locally, trying peer instances", id);
            patient = patientPeerService.findPatientWithFallback(id, null);
        }
        
        if (patient.isEmpty() || !patient.get().isEnabled()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(patient.get());
    }

    public ResponseEntity<Patient> getPatientByNumber(String number) {
        if (number == null || number.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        String trimmedNumber = number.trim();
        Optional<Patient> patient = patientRepository.findByPatientNumber(trimmedNumber);
        
        if (patient.isEmpty()) {
            log.debug("Patient {} not found locally, trying peer instances", trimmedNumber);
            patient = patientPeerService.findPatientWithFallback(null, trimmedNumber);
        }
        
        if (patient.isEmpty() || !patient.get().isEnabled()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(patient.get());
    }

    public List<Patient> searchByPatientName(String name, Page page) {
        if (page == null) {
            page = new Page(1, 5);
        }
        return patientRepository.findByName(name);
    }

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public Optional<Patient> findByEmailAddress(String emailAddress) {
        return patientRepository.findByEmailAddress(emailAddress);
    }

    /**
     * Get patient by ID from local database only (no fallback to peers)
     * Used by internal replication endpoints
     */
    public ResponseEntity<Patient> getPatientByIdLocalOnly(Long id) {
        Optional<Patient> patient = patientRepository.findById(id);
        if (patient.isEmpty() || !patient.get().isEnabled()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(patient.get());
    }

    /**
     * Get patient by number from local database only (no fallback to peers)
     * Used by internal replication endpoints
     */
    public ResponseEntity<Patient> getPatientByNumberLocalOnly(String number) {
        if (number == null || number.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Patient> patient = patientRepository.findByPatientNumber(number.trim());
        if (patient.isEmpty() || !patient.get().isEnabled()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(patient.get());
    }
}

