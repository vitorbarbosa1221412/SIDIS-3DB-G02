package com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.services;

import com.example.psoft25_1221392_1211686_1220806_1211104.exceptions.ConflictException;
import com.example.psoft25_1221392_1211686_1220806_1211104.exceptions.NotFoundException;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.model.*;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.repositories.PatientRepository;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.model.Patient;
import com.example.psoft25_1221392_1211686_1220806_1211104.usermanagement.model.Role;
import com.example.psoft25_1221392_1211686_1220806_1211104.usermanagement.model.User;
import com.example.psoft25_1221392_1211686_1220806_1211104.usermanagement.repositories.UserRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;

    private final PatientEditMapper mapper;

    private final UserRepository userRepository;

    private final Password passVal = new Password();

    private final PatientNumber patientNumGen = new PatientNumber();

    private final PasswordEncoder passwordEncoder;

    private final Name nameVal = new Name();

    @Override
    public Patient createPatient(CreatePatientRequest request) {
        if (patientRepository.findByEmailAddress(request.getEmailAddress()).isPresent()) {
            throw new ConflictException("Username already exists!");
        }
        if (!request.getPassword().equals(request.getRePassword())) {
            throw new ValidationException("Passwords don't match!");
        }
        if(!passVal.validate(request.getPassword())){
            throw new ValidationException("Password is not valid! It must contain at least one uppercase char, one special character or number and cannot exceed the limit of 8 characters!");
        }

        final Patient patient = mapper.create(request);

        patient.setPatientNumber(patientNumGen.generate(patientNumGen.getNextPatientNumber(patientRepository)));

        User user = User.newUser(patient.getEmailAddress(),passwordEncoder.encode(request.getPassword()),patient.getName(), Role.PATIENT);
        userRepository.save(user);
        patient.setUser(user);

        return patientRepository.save(patient);
    }

    @Override
    public ResponseEntity<Patient> getPatientById(Long id) {
        Patient patient = patientRepository.findById(id).orElse(null);
        if (patient == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found");
        } else {
            return new ResponseEntity<>(patient, HttpStatus.OK);
        }
    }

    @Override
    public List<Patient> searchByPatientName(String name, Page page) {
        if (page == null) {
            page = new Page(1, 5);
        }

        if(!nameVal.validate(name))
        {
            throw new ValidationException("Name is not valid! It must contain no special characters and there is a limit of 150 characters.");
        }

        final List<Patient> patients = patientRepository.findByName(name);
        if (patients.isEmpty()) {
            throw new NotFoundException("Patient with name " + name + " not found");
        }
        return patients;
    }

    @Override
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    @Override
    public void updatePersonalData(String username, UpdatePatientRequest request) {
        Patient patient = patientRepository.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        patient.setPhoneNumber(request.getPhoneNumber());
        patient.setAddress(request.getAddress());
        patientRepository.save(patient);
    }

    @Override
    public ResponseEntity<Patient> getPatientByNumber(String number) {
        if (number == null || number.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Patient> patient = patientRepository.findByPatientNumber(number.trim());

        return patient.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
