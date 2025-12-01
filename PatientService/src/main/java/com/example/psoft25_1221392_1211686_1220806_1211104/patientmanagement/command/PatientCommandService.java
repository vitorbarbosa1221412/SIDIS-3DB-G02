package com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.command;

import com.example.psoft25_1221392_1211686_1220806_1211104.exceptions.ConflictException;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.model.Patient;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.messaging.publisher.PatientEventPublisher;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.messaging.dto.PatientCreatedEvent;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.messaging.dto.PatientUpdatedEvent;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.messaging.dto.PatientDeletedEvent;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.repositories.PatientRepository;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.services.CreatePatientRequest;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.services.PatientEditMapper;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.services.UpdatePatientRequest;
import com.example.psoft25_1221392_1211686_1220806_1211104.usermanagement.model.*;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.model.*;
import com.example.psoft25_1221392_1211686_1220806_1211104.usermanagement.model.Role;
import com.example.psoft25_1221392_1211686_1220806_1211104.usermanagement.model.User;
import com.example.psoft25_1221392_1211686_1220806_1211104.usermanagement.repositories.UserRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Command Service - Handles all write operations (CQRS Command Side)
 * Uses the existing Patient model and PatientRepository
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PatientCommandService {

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PatientEventPublisher eventPublisher;
    private final PatientEditMapper mapper;
    private final Password passVal = new Password();
    private final com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.model.PatientNumber patientNumGen = 
        new com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.model.PatientNumber();

    @Transactional
    public Patient createPatient(CreatePatientRequest request) {
        // Validation
        if (patientRepository.findByEmailAddress(request.getEmailAddress()).isPresent()) {
            throw new ConflictException("Email address already exists!");
        }
        
        if (!request.getPassword().equals(request.getRePassword())) {
            throw new ValidationException("Passwords don't match!");
        }
        
        if (!passVal.validate(request.getPassword())) {
            throw new ValidationException("Password is not valid! It must contain at least one uppercase char, one special character or number and cannot exceed the limit of 8 characters!");
        }

        // Create patient using existing mapper
        final Patient patient = mapper.create(request);
        patient.setPatientNumber(patientNumGen.generate(patientNumGen.getNextPatientNumber(patientRepository)));

        // Create user
        User user = User.newUser(
            patient.getEmailAddress(),
            passwordEncoder.encode(request.getPassword()),
            patient.getName(),
            Role.PATIENT
        );
        user = userRepository.save(user);
        patient.setUser(user);

        // Save patient
        Patient savedPatient = patientRepository.save(patient);

        // Publish event asynchronously (CQRS - event-driven update)
        PatientCreatedEvent event = PatientCreatedEvent.builder()
            .patientId(savedPatient.getId())
            .patientNumber(savedPatient.getPatientNumber())
            .emailAddress(savedPatient.getEmailAddress())
            .name(savedPatient.getName())
            .timestamp(LocalDateTime.now())
            .build();
        
        eventPublisher.publishPatientCreated(event);
        log.info("Patient created and event published: {}", savedPatient.getPatientNumber());

        return savedPatient;
    }

    @Transactional
    public void updatePatient(String username, UpdatePatientRequest request) {
        Patient patient = patientRepository.findByUserUsername(username)
            .orElseThrow(() -> new RuntimeException("Patient not found"));

        patient.setPhoneNumber(request.getPhoneNumber());
        patient.setAddress(request.getAddress());
        Patient updatedPatient = patientRepository.save(patient);

        // Publish event
        PatientUpdatedEvent event = PatientUpdatedEvent.builder()
            .patientId(updatedPatient.getId())
            .patientNumber(updatedPatient.getPatientNumber())
            .phoneNumber(updatedPatient.getPhoneNumber())
            .address(updatedPatient.getAddress())
            .timestamp(LocalDateTime.now())
            .build();
        
        eventPublisher.publishPatientUpdated(event);
        log.info("Patient updated and event published: {}", updatedPatient.getPatientNumber());
    }

   /* @Transactional
    public void deletePatient(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new RuntimeException("Patient not found"));

        patientRepository.delete(patient);

        // Publish event
        PatientDeletedEvent event = PatientDeletedEvent.builder()
            .patientId(patient.getId())
            .patientNumber(patient.getPatientNumber())
            .timestamp(LocalDateTime.now())
            .build();
        
        eventPublisher.publishPatientDeleted(event);
        log.info("Patient deleted and event published: {}", patient.getPatientNumber());
    }*/
}

