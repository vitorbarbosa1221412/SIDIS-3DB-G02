package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.services;

import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.model.Physician;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface PhysicianService {
    Physician createPhysician(CreatePhysicianRequest request, MultipartFile imageFile);

    ResponseEntity<Physician> getPhysicianById(Long physicianId);

    List<Physician> getAllPhysicians(Page Page);

    ResponseEntity<List<Physician>> searchPhysiciansByName(Page page, String name);

    ResponseEntity<List<Physician>> searchPhysiciansBySpecialty(Page page, String specialty);

    Physician updatePhysician(String physicianNumber, UpdatePhysicianRequest request);

    List<Physician> searchTop5Physicians();
}
