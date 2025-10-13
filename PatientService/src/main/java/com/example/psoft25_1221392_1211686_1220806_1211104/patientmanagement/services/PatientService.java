package com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.services;

import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.model.Patient;
//import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.model.Physician;
import com.example.psoft25_1221392_1211686_1220806_1211104.usermanagement.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface PatientService {

    Patient createPatient(CreatePatientRequest request);

    ResponseEntity<Patient> getPatientById(Long id);

    ResponseEntity<Patient> getPatientByNumber(String number);

    List<Patient> searchByPatientName(String name, Page page);

    List<Patient> getAllPatients();

    void updatePersonalData(String username, UpdatePatientRequest request);


}
