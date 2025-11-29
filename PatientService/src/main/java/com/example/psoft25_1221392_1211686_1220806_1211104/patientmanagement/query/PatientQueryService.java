package com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.query;

import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.model.Patient;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.repositories.PatientRepository;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.services.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Query Service - Handles all read operations (CQRS Query Side)
 * Uses the existing Patient model and PatientRepository
 * This separation allows for optimized read operations and eventual consistency
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PatientQueryService {

    private final PatientRepository patientRepository;

    public ResponseEntity<Patient> getPatientById(Long id) {
        Optional<Patient> patient = patientRepository.findById(id);
        if (patient.isEmpty() || !patient.get().isEnabled()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(patient.get());
    }

    public ResponseEntity<Patient> getPatientByNumber(String number) {
        if (number == null || number.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Patient> patient = patientRepository.findByPatientNumber(number.trim());
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
}

